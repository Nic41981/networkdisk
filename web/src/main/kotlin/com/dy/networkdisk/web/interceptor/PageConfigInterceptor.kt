package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.web.tool.ConfigUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class PageConfigInterceptor @Autowired constructor(
        val config: ConfigUtil
): HandlerInterceptorAdapter() {

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        modelAndView?.let {
            val title = config.getString(ConfigRedisKey.WEB_TITLE,"青叶网盘")
            it.addObject("title",title)
        }
    }
}