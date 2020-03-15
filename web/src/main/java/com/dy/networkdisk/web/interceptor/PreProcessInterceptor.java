package com.dy.networkdisk.web.interceptor;

import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.tool.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
public class PreProcessInterceptor extends HandlerInterceptorAdapter {

    /**
     * 代理下用户IP信息头
     */
    private static final String[] REQUEST_IP_HEADER_KEY = new String[]{
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    private static final String UNKNOWN_IP = "unknown";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //处理IP信息
        request.setAttribute(Const.IP_KEY,findIP(request));
        //处理Token信息
        String token = findToken(request);
        if (token != null){
            request.setAttribute(Const.ONLINE_TOKEN_KEY,token);
            return true;
        }
        //Token不存在
        token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(Const.ONLINE_TOKEN_KEY,token);
        response.addCookie(cookie);
        cookie.setPath("/");
        response.sendRedirect(request.getRequestURI());
        return false;
    }

    /**
     * 获取用户IP
     * 适配代理和反向代理
     * @param request 用户请求
     * @return 用户IP/unknown
     */
    private String findIP(HttpServletRequest request){
        String ip = UNKNOWN_IP;
        //查找代理IP
        for (String key : REQUEST_IP_HEADER_KEY){
            ip = request.getHeader(key);
            if (!StringUtil.isNull(ip) && !ip.equalsIgnoreCase(UNKNOWN_IP)){
                break;
            }
        }
        //无代理,查找用户IP
        if (StringUtil.isNull(ip) || ip.equalsIgnoreCase(UNKNOWN_IP)){
            ip = request.getRemoteAddr();
        }
        if (StringUtil.isNull(ip) || ip.equalsIgnoreCase(UNKNOWN_IP)){
            return UNKNOWN_IP;
        }
        //处理多级代理
        if (ip.contains(",")){
            String[] ipArray = ip.split(",");
            ip = UNKNOWN_IP;
            for (String it : ipArray){
                if (!StringUtil.isNull(it) && !it.equalsIgnoreCase(UNKNOWN_IP)){
                    ip = it;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 查找用户在线Token
     * @param request 用户请求
     * @return 在线Token/null
     */
    public String findToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Const.ONLINE_TOKEN_KEY)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
