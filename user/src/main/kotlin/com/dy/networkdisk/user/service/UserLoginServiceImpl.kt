package com.dy.networkdisk.user.service

import com.dy.networkdisk.api.dto.dubbo.user.LoginDTO
import com.dy.networkdisk.api.dto.dubbo.user.LoginResult
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.user.config.Const
import com.dy.networkdisk.user.dao.LoginMapper
import com.dy.networkdisk.user.dao.UserMapper
import com.dy.networkdisk.user.po.LoginPO
import com.dy.networkdisk.user.tool.IDWorker
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import javax.annotation.Resource
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService
class UserLoginServiceImpl @Autowired constructor(
        private val template: StringRedisTemplate,
        private val idWorker: IDWorker
) : UserLoginService {

    @Resource
    private lateinit var userMapper: UserMapper

    @Resource
    private lateinit var loginMapper: LoginMapper

    override fun getUserType(email: String): String {
        val key = "${Const.FUNC_GUESTS_REDIS_KEY}:${email}"
        if (template.hasKey(key)){
            return Const.USER_TYPE_GUESTS
        }
        return when(userMapper.findTypeByEmail(email)){
            Const.USER_TYPE_NORMAL -> Const.USER_TYPE_NORMAL
            Const.USER_TYPE_ADMIN -> Const.USER_TYPE_ADMIN
            Const.USER_TYPE_ROOT -> Const.USER_TYPE_ROOT
            else -> Const.USER_TYPE_UNKNOWN
        }
    }

    override fun login(dto: LoginDTO): LoginResult {
        val userInfo = userMapper.login(dto.email) ?: return LoginResult(false).apply {
            content = "用户不存在！"
            type = "error"
        }
        val lockKey = "${Const.FUNC_USER_LOCK_REDIS_KEY}:${userInfo.id}"
        //用户封禁检查
        if (template.hasKey(lockKey)){
            val redisOps = template.opsForHash<String,String>()
            return LoginResult(false).apply {
                content = "该账号已被管理员封禁！</br>" +
                        "封禁时间：${redisOps[lockKey,"start"]}-${redisOps[lockKey,"end"]}</br>" +
                        "封禁原因：${redisOps[lockKey,"reason"] ?: "无"}"
                type = "info"
            }
        }
        //密码检查
        if (BCrypt.checkpw(dto.password,userInfo.password)){
            //记录用户信息
            val key = "${Const.FUNC_USER_ONLINE_REDIS_KEY}:${dto.token}"
            template.opsForHash<String,String>().run {
                put(key,"id",userInfo.id.toString())
                put(key,"email",userInfo.email)
                put(key,"nickname", userInfo.nickname)
                put(key,"type",userInfo.type)
            }
            loginMapper.insert(LoginPO().apply {
                this.id = idWorker.nextId()
                this.userID = userInfo.id
                this.loginIP = dto.ip
                this.loginIPLocation = dto.ipLocation
                this.format()
            })
            return LoginResult(true)
        }
        return LoginResult(false).apply {
            content = "用户名或密码错误！"
            type = "warning"
        }
    }
}