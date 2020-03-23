package com.dy.networkdisk.web.interceptor

import com.dy.networkdisk.web.config.Const
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class IPInterceptor: HandlerInterceptorAdapter() {

    private val proxyHeaders = arrayOf(
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    )

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        var ip = getProxyIP(request)
        if (ip?.contains(",") == true){
            ip = parserMultiProxy(ip)
        }
        if (ip == null){
            ip = request.remoteAddr
        }
        if (ip.isNullOrBlank()){
            ip = "unknown"
        }
        request.setAttribute(Const.IP_KEY,ip)
        return true
    }

    private fun getProxyIP(request: HttpServletRequest): String?{
        for (header: String in proxyHeaders){
            val value = request.getHeader(header)
            if (!value.isNullOrBlank() && !value.equals("unknown",true)) {
                return value
            }
        }
        return null
    }

    private fun parserMultiProxy(ipStr: String): String?{
        for (ip: String in ipStr.split(",")){
            if (!ip.isBlank() && !ip.equals("unknown",true)){
                return ip
            }
        }
        return null
    }
}