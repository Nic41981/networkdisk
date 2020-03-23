package com.dy.networkdisk.web.config

import com.dy.networkdisk.web.interceptor.IPInterceptor
import com.dy.networkdisk.web.interceptor.OnlineTokenInterceptor
import com.dy.networkdisk.web.interceptor.PageConfigInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig @Autowired constructor(
        val ip: IPInterceptor,
        val onlineToken: OnlineTokenInterceptor,
        val pageConfig: PageConfigInterceptor
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        //IP信息注入
        registry.addInterceptor(ip)
                .addPathPatterns("/**")
        //在线token维持
        registry.addInterceptor(onlineToken)
                .addPathPatterns("/**")
                .excludePathPatterns("/tool/**")
        //页面信息注入
        registry.addInterceptor(pageConfig)
                .addPathPatterns("/**")
    }
}