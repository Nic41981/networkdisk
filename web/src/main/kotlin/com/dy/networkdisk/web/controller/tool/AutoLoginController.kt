package com.dy.networkdisk.web.controller.tool

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.user.AutoLoginDTO
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.web.bean.QYSessionInfo
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.*
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/tool")
class AutoLoginController @Autowired constructor(
        private val template: StringRedisTemplate,
        private val config: ConfigUtil
) {

    @Reference
    private lateinit var service: UserLoginService

    /*****扩展*****/
    private val HttpServletRequest.ip: String
        get() {
            return getAttribute(Const.IP_KEY) as String
        }
    private val HttpServletRequest.sessionID: Long
        get() {
            return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
        }

    @GetMapping("/autologin")
    fun autoLogin(request: HttpServletRequest,response: HttpServletResponse){
        val ipLocation = IPLocation(request.ip)
        val token = request.getAttribute(Const.AUTOLOGIN_KEY) as Long
        val target = request.getAttribute(Const.REDIRECT_TARGET) as String
        val autologinKey = "${Const.FUNC_AUTOLOGIN_REDIS_KET}:${token}"
        val value = template.opsForValue()[autologinKey] ?: return fail(response,target)
        val sessionInfo = value.fromJson<QYSessionInfo>() ?: return fail(response,target)
        val result = service.autoLogin(AutoLoginDTO(
                id = sessionInfo.id,
                ip = ipLocation.ip,
                ipLocation = ipLocation.getLocation()
        ))
        if (!result){
            return fail(response,target)
        }
        val onlineExpire = config.getLong(ConfigInfo.USER_ONLINE_EXPIRE,30)
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${request.sessionID}"
        template.opsForHash<String,String>().put(sessionKey,"info",value)
        template.expire(sessionKey,onlineExpire, TimeUnit.MINUTES)
        response.sendRedirect(target)
    }

    private fun fail(response: HttpServletResponse, target: String){
        //删除无效信息
        val cookie = Cookie(Const.AUTOLOGIN_KEY,null)
        cookie.maxAge = 0
        cookie.path = "/"
        response.addCookie(cookie)
        response.sendRedirect(target)
    }
}