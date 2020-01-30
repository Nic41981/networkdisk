package com.dy.networkdisk.web.interceptor;

import com.dy.networkdisk.web.config.Const;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Const.ONLINE_TOKEN_KEY)) {
                    request.setAttribute(Const.ONLINE_TOKEN_KEY,cookie.getValue());
                    return true;
                }
            }
        }
        request.setAttribute(Const.TARGET_PATH,request.getRequestURI());
        request.getRequestDispatcher("/tool/addToken").forward(request,response);
        return false;
    }
}
