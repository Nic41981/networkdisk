package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.web.config.Const
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class NeedNotLoginInterceptor @Autowired constructor(
        private val template: StringRedisTemplate
): HandlerInterceptorAdapter() {

    /**
     * 扩展
     */
    private val HttpServletRequest.sessionID: Long
        get() {
            return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
        }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${request.sessionID}"
        if (template.hasKey(sessionKey)){
            //用户已登录,跳转至根目录
            request.setAttribute(Const.REDIRECT_TARGET,"/")
            request.getRequestDispatcher("/tool/redirect").forward(request,response)
            return false
        }
        //未登录不做拦截
        return true
    }
}