package com.dy.networkdisk.web.controller.user;

import com.dy.networkdisk.api.user.VerificationService;
import com.dy.networkdisk.web.config.Const;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Controller
public class VerificationController {

    @Reference
    private VerificationService service;

    @GetMapping("/verification")
    public void getVerificationImage(HttpServletRequest request, HttpServletResponse response){
        String token = (String)request.getAttribute(Const.ONLINE_TOKEN_KEY);
        if (token != null){
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            try(ServletOutputStream out = response.getOutputStream()){
                byte[] imageBytes = service.getVerificationCode(token);
                ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(is);
                ImageIO.write(image,"jpg",out);
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
