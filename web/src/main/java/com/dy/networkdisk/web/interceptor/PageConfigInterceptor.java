package com.dy.networkdisk.web.interceptor;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.web.tool.ConfigUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PageConfigInterceptor extends HandlerInterceptorAdapter {

    private final ConfigUtil config;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        if (modelAndView != null) {
            modelAndView.addObject("title", config.getString(ConfigRedisKey.WEB_TITLE, "青叶网盘"));
        }
    }
}
