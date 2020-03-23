package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.web.config.Const
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OnlineTokenInterceptor: HandlerInterceptorAdapter() {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = findOnlineToken(request)
        if (token.isNullOrBlank()){
            //Token不存在
            val cookie  = Cookie(Const.ONLINE_TOKEN_KEY,UUID.randomUUID().toString())
            cookie.path = "/"
            response.addCookie(cookie)
            request.setAttribute(Const.REDIRECT_TARGET, request.requestURI)
            request.getRequestDispatcher("/tool/redirect").forward(request, response)
            return false
        }
        request.setAttribute(Const.ONLINE_TOKEN_KEY,token)
        return true
    }

    private fun findOnlineToken(request: HttpServletRequest): String?{
        val cookies = request.cookies
        for (cookie in cookies){
            if (cookie.name == Const.ONLINE_TOKEN_KEY){
                return cookie.value
            }
        }
        return null
    }
}