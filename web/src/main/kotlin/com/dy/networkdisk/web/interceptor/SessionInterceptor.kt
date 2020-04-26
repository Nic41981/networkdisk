package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.IDWorker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SessionInterceptor @Autowired constructor(
        private val idWorker: IDWorker
): HandlerInterceptorAdapter() {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val sessionID = findSessionID(request)
        if (sessionID != null) {
            request.setAttribute(Const.SESSION_KEY, sessionID.toLong(16))
            return true
        }
        //Token不存在
        val cookie = Cookie(Const.SESSION_KEY, idWorker.nextId().toString(16))
        cookie.path = "/"
        response.addCookie(cookie)
        request.setAttribute(Const.REDIRECT_TARGET, request.requestURI)
        request.getRequestDispatcher("/tool/redirect").forward(request, response)
        return false
    }

    private fun findSessionID(request: HttpServletRequest): String?{
        val cookies = request.cookies
        for (cookie in cookies){
            if (cookie.name == Const.SESSION_KEY && !cookie.value.isNullOrBlank()){
                return cookie.value
            }
        }
        return null
    }
}