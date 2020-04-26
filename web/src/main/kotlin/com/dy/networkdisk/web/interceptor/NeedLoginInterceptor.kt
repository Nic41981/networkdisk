package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.ConfigUtil
import com.dy.networkdisk.web.tool.sessionID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class NeedLoginInterceptor @Autowired constructor(
        private val template: StringRedisTemplate,
        private val config: ConfigUtil
) : HandlerInterceptorAdapter() {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${request.sessionID}"
        if (!template.hasKey(sessionKey)){
            //尝试自动登录
            val token = findToken(request)
            if (token.isNullOrBlank()){
                //无法自动登录，转至密码登录
                request.setAttribute(Const.REDIRECT_TARGET,"/user/login")
                request.getRequestDispatcher("/tool/redirect").forward(request,response)
                return false
            }
            //跳转自动登录
            request.setAttribute(Const.AUTOLOGIN_KEY,token.toLong(16))
            request.setAttribute(Const.REDIRECT_TARGET,request.requestURI)
            request.getRequestDispatcher("/tool/autoLogin").forward(request,response)
            return false
        }
        //已登录用户更新有效期
        val expire = config.getLong(ConfigInfo.USER_ONLINE_EXPIRE,30)
        template.expire(sessionKey,expire,TimeUnit.MINUTES)
        return true
    }

    private fun findToken(request: HttpServletRequest): String?{
        val cookies = request.cookies
        for (cookie in cookies){
            if (cookie.name == Const.AUTOLOGIN_KEY){
                return cookie.value
            }
        }
        return null
    }
}