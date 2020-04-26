package com.dy.networkdisk.user.service

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.email.RemoteLoginEmailDTO
import com.dy.networkdisk.api.dto.user.AutoLoginDTO
import com.dy.networkdisk.api.dto.user.LoginDTO
import com.dy.networkdisk.api.dto.user.LoginResult
import com.dy.networkdisk.api.email.UserEmailService
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.user.config.Const
import com.dy.networkdisk.user.dao.LoginMapper
import com.dy.networkdisk.user.dao.UserMapper
import com.dy.networkdisk.user.po.LoginPO
import com.dy.networkdisk.user.tool.IDWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.dubbo.config.annotation.Reference
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
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

    @Reference
    private lateinit var emailService: UserEmailService

    override fun isGuest(email: String, password: String): Boolean {
        val key = "${Const.FUNC_GUESTS_REDIS_KEY}:${email}"
        val value = template.opsForHash<String,String>()[key,"password"] ?: return false
        return BCrypt.checkpw(password,value)
    }

    override fun login(dto: LoginDTO): QYResult<LoginResult> {
        val userInfo = userMapper.login(dto.email) ?: return QYResult.fail(msg = "用户名或密码错误！")
        //用户封禁检查
        val lockKey = "${Const.FUNC_USER_LOCK_REDIS_KEY}:${userInfo.id}"
        if (template.hasKey(lockKey)) {
            val redisOps = template.opsForHash<String, String>()
            return QYResult.fail(
                    msg = "该账号已被管理员封禁！</br>" +
                    "封禁时间：${redisOps[lockKey, "start"]}-${redisOps[lockKey, "end"]}</br>" +
                    "封禁原因：${redisOps[lockKey, "reason"] ?: "无"}"
            )
        }
        //密码检查
        if (!BCrypt.checkpw(dto.password, userInfo.password)) {
            return QYResult.fail(msg = "用户名或密码错误！")
        }
        //异地登录拦截
        GlobalScope.launch(Dispatchers.IO){
            if ("unknown" != dto.ipLocation){
                val lastLocation = loginMapper.findLastIPLocationByUserID(userInfo.id) ?: "unknown"
                if ("unknown" != lastLocation){
                    if (lastLocation != dto.ipLocation){
                        emailService.remoteLogin(RemoteLoginEmailDTO(
                                email = userInfo.email,
                                nickname = userInfo.nickname,
                                lastLocation = lastLocation,
                                thisLocation = dto.ipLocation,
                                loginIP = dto.ip
                        ))
                    }
                }
            }
        }
        //记录用户登录
        loginMapper.insert(LoginPO(
                id = idWorker.nextId(),
                userID = userInfo.id,
                loginTime = Date(),
                loginIP = dto.ip,
                loginIPLocation = dto.ipLocation,
                type = "web|password"
        ))
        //更新在线信息
        return QYResult.success(data = LoginResult(
                id = userInfo.id,
                email = userInfo.email,
                nickname = userInfo.nickname,
                type = userInfo.type
        ))
    }

    override fun autoLogin(dto: AutoLoginDTO): Boolean {
        //异地登录拦截
        if ("unknown" != dto.ipLocation){
            val lastLocation = loginMapper.findLastIPLocationByUserID(dto.id) ?: "unknown"
            if ("unknown" != lastLocation){
                if (lastLocation != dto.ipLocation){
                    return false
                }
            }
        }
        //记录登录信息
        loginMapper.insert(LoginPO(
                id = idWorker.nextId(),
                userID = dto.id,
                loginTime = Date(),
                loginIP = dto.ip,
                loginIPLocation = dto.ipLocation,
                type = "web|auto"
        ))
        return true
    }
}