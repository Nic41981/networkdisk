package com.dy.networkdisk.web.controller.user;

import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.tool.KaptchaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationController {

    private final KaptchaUtil kaptcha;

    @GetMapping("/verification")
    public void getVerificationImage(HttpServletRequest request, HttpServletResponse response){
        String token = (String)request.getAttribute(Const.ONLINE_TOKEN_KEY);
        if (token != null){
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String code = kaptcha.createText(token);
            try(ServletOutputStream out = response.getOutputStream()){
                ImageIO.write(kaptcha.createImage(code),"jpg",out);
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
