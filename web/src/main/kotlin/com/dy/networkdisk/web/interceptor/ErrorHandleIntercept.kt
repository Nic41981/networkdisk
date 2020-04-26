package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.web.vo.MessagePageVO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ErrorHandleIntercept: HandlerInterceptorAdapter() {

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        val code = response.status
        if (code in 400..600){
            val reason = try {
                HttpStatus.valueOf(response.status).reasonPhrase
            } catch (e: Exception){
                "Unknown Error"
            }
            modelAndView?.apply {
                viewName = "message"
                addObject(MessagePageVO.NAME,MessagePageVO("错误")
                        .requestError(code,reason).withTime().jumpTo("首页","/")
                )
            }
        }
    }
}