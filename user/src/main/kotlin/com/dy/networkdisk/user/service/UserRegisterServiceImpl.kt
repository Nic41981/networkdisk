package com.dy.networkdisk.user.service

import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.email.ActiveEmailDTO
import com.dy.networkdisk.api.dto.user.ActiveDTO
import com.dy.networkdisk.api.dto.user.RegisterDTO
import com.dy.networkdisk.api.email.UserEmailService
import com.dy.networkdisk.api.user.UserRegisterService
import com.dy.networkdisk.user.bo.GuestsBO
import com.dy.networkdisk.user.config.Const
import com.dy.networkdisk.user.dao.UserMapper
import com.dy.networkdisk.user.po.UserPO
import com.dy.networkdisk.user.tool.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.dubbo.config.annotation.Reference
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService
class UserRegisterServiceImpl @Autowired constructor(
        private val template: StringRedisTemplate,
        private val config: ConfigUtil,
        private val idWorker: IDWorker
) : UserRegisterService {
    
    @Resource
    private lateinit var mapper: UserMapper

    @Reference
    private lateinit var emailService: UserEmailService

    override fun register(dto: RegisterDTO): Boolean {
        val bo = GuestsBO().apply {
            email = dto.email
            nickname = dto.nickname
            password = BCrypt.hashpw(dto.password,BCrypt.gensalt())
            inviterID = dto.inviterID
            registerTime = Date()
            registerIP = dto.ip
            registerIPLocation = dto.ipLocation
        }
        val key = "${Const.FUNC_GUESTS_REDIS_KEY}:${bo.email}"
        val lock = UUID.randomUUID().toString()
        val password = bo.password
        val value = GsonUtil.toJson(bo)
        val expire = config.getLong(ConfigInfo.USER_GUESTS_EXPIRE, 12L)
        //信息加锁
        val result = template.opsForHash<String,String>().putIfAbsent(key,"lock",lock)
        if (!result) {
            //加锁失败，用户已存在
            return false
        }
        //加锁成功，存入用户信息
        template.opsForHash<String,String>().put(key,"password",password)
        template.opsForHash<String,String>().put(key,"info",value)
        template.expire(key,expire,TimeUnit.HOURS)
        //数据库查询
        if (mapper.checkEmailExist(bo.email) != 0) {
            //用户已存在
            template.delete(key)
            return false
        }
        //发送激活邮件
        GlobalScope.launch(Dispatchers.IO) {
            emailService.activeAccount(ActiveEmailDTO(
                    email = bo.email,
                    nickname = bo.nickname,
                    registerTime = bo.registerTime,
                    registerIP = bo.registerIP,
                    registerIPLocation = bo.registerIPLocation,
                    lock = lock,
                    activeURL = "${dto.host}/user/register/active"
            ))
        }
        return true
    }

    override fun active(dto: ActiveDTO): Boolean {
        val key = Const.FUNC_GUESTS_REDIS_KEY + ":" + dto.email
        val lock = template.opsForHash<String,String>()[key,"lock"]
        if (lock.isNullOrBlank()) {
            //游客信息不存在或失效
            return false
        }
        if (lock != dto.lock) {
            //秘钥匹配失败或信息失效
            return false
        }
        //用户信息写入数据库
        val info = template.opsForHash<String,String>()[key,"info"]
        if (info.isNullOrBlank()){
            //用户信息丢失
            return false
        }
        val bo = GsonUtil.fromJson<GuestsBO>(info,GuestsBO::class.java) ?: return false
        val po = UserPO().apply {
            this.id = idWorker.nextId()
            this.email = bo.email
            this.nickname = bo.nickname
            this.password = bo.password
            this.type = Const.USER_TYPE_NORMAL
            this.inviterID = bo.inviterID
            this.registerTime = bo.registerTime
            this.registerIP = bo.registerIP
            this.registerIPLocation = bo.registerIPLocation
            this.activeTime = Date()
            this.activeIP = dto.ip
            this.activeIPLocation = dto.ipLocation
            this.status = Const.USER_STATUS_NORMAL
            this.format()
        }
        mapper.register(po)
        //删除游客信息避免二次激活
        template.delete(key)
        return true
    }
}