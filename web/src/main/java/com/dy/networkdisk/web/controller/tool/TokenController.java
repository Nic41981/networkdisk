package com.dy.networkdisk.web.controller.tool;

import com.dy.networkdisk.web.config.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
@Slf4j
public class TokenController {

    /**
     * 为初次访问者添加在线识别token
     */
    @GetMapping("/tool/addToken")
    public void setOnlineToken(HttpServletRequest request, HttpServletResponse response){
        String token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(Const.ONLINE_TOKEN_KEY,token);
        response.addCookie(cookie);
        try {
            response.sendRedirect((String)request.getAttribute(Const.TARGET_PATH));
        } catch (IOException e) {
            log.error("重定向失败",e);
        }
    }
}
