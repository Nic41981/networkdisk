package com.dy.networkdisk.web.config;

import com.dy.networkdisk.web.interceptor.PageConfigInterceptor;
import com.dy.networkdisk.web.interceptor.PreProcessInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebConfig implements WebMvcConfigurer {

    //请求信息预处理
    private final PreProcessInterceptor preProcessInterceptor;
    //页面通用信息注入
    private final PageConfigInterceptor pageConfigInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(preProcessInterceptor).addPathPatterns("/**");
        registry.addInterceptor(pageConfigInterceptor).addPathPatterns("/**");
    }
}
