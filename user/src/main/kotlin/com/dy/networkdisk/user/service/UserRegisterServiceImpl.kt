package com.dy.networkdisk.user.service

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.api.config.QueueConst
import com.dy.networkdisk.api.dto.dubbo.user.ActiveDTO
import com.dy.networkdisk.api.dto.dubbo.user.RegisterDTO
import com.dy.networkdisk.api.dto.mq.email.UserActiveEmailDTO
import com.dy.networkdisk.api.user.UserRegisterService
import com.dy.networkdisk.user.bo.GuestsBO
import com.dy.networkdisk.user.config.Const
import com.dy.networkdisk.user.dao.UserMapper
import com.dy.networkdisk.user.po.UserPO
import com.dy.networkdisk.user.tool.*
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService
class UserRegisterServiceImpl @Autowired constructor(
        private val template: StringRedisTemplate,
        private val jmsTemplate: JmsTemplate,
        private val config: ConfigUtil,
        private val idWorker: IDWorker
) : UserRegisterService {
    
    @Resource
    private lateinit var mapper: UserMapper
    
    override fun register(dto: RegisterDTO): Boolean {
        //redis加锁
        val bo = GuestsBO().apply {
            email = dto.email
            nickname = dto.nickname
            password = BCrypt.hashpw(dto.password,BCrypt.gensalt())
            inviterID = dto.inviterID
            registerTime = Date()
            registerIP = dto.ip
            registerIPLocation = dto.ipLocation
            lock = UUID.randomUUID().toString()
        }
        val key = "${Const.FUNC_GUESTS_REDIS_KEY}:${bo.email}"
        val value = GsonUtil.toJson(bo)
        val expire = config.getLong(ConfigRedisKey.USER_GUESTS_EXPIRE, 12L)
        val result = template.opsForValue().setIfAbsent(key, value, expire, TimeUnit.HOURS)
        if (result == null || !result) {
            //加锁失败，用户已存在
            return false
        }
        //数据库查询
        if (mapper.checkEmailExist(bo.email) != 0) {
            //用户已存在
            template.delete(key)
            return false
        }
        //发送激活邮件
        val email = UserActiveEmailDTO().apply {
            email = bo.email
            nickname = bo.nickname
            registerTime = bo.registerTime
            registerIP = bo.registerIP
            registerIPLocation = bo.registerIPLocation
            lock = bo.lock
        }
        email.activeURL = dto.host + "/user/register/active"
        jmsTemplate.convertAndSend(QueueUtil.getQueue(QueueConst.activeMailQueue), GsonUtil.toJson(email))
        return true
    }

    override fun active(dto: ActiveDTO): Boolean {
        val key = Const.FUNC_GUESTS_REDIS_KEY + ":" + dto.email
        val value = template.opsForValue()[key]
        if (value.isNullOrBlank()) {
            //游客信息不存在或失效
            return false
        }
        val bo = GsonUtil.fromJson<GuestsBO>(value, GuestsBO::class.java) ?: return false
        if (bo.lock != dto.lock) {
            //秘钥匹配失败
            return false
        }
        //用户信息写入数据库
        val po = UserPO().apply {
            id = idWorker.nextId()
            email = bo.email
            nickname = bo.nickname
            password = bo.password
            inviterID = bo.inviterID
            registerTime = bo.registerTime
            registerIP = bo.registerIP
            activeIP = dto.ip
            activeTime = Date()
            state = Const.USER_STATUS_NORMAL
        }
        mapper.register(po)
        //删除游客信息避免二次激活
        template.delete(key)
        return true
    }
}