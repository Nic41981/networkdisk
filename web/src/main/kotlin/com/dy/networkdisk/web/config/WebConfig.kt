package com.dy.networkdisk.web.config

import com.dy.networkdisk.web.interceptor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig @Autowired constructor(
        private val ip: IPInterceptor,
        private val session: SessionInterceptor,
        private val needLogin: NeedLoginInterceptor,
        private val needNotLogin: NeedNotLoginInterceptor,
        private val pageConfig: PageConfigInterceptor,
        private val errorHandle: ErrorHandleIntercept
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        //会话信息维持
        registry.addInterceptor(session)
                .addPathPatterns("/**")
                .excludePathPatterns("/tool/redirect")
        //IP信息注入
        registry.addInterceptor(ip)
                .addPathPatterns("/**")
        //需要拦截未登录
        registry.addInterceptor(needLogin)
                .addPathPatterns("/","/file/**")
        //需要拦截已登录
        registry.addInterceptor(needNotLogin)
                .addPathPatterns("/user/login")
                .addPathPatterns("/user/register")
                .addPathPatterns("/user/register/active")
        //页面信息注入
        registry.addInterceptor(pageConfig)
                .addPathPatterns("/**")
//        //错误处理
//        registry.addInterceptor(errorHandle)
//                .addPathPatterns("/**")
    }
}