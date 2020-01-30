package com.dy.networkdisk.user.service;

import com.dy.networkdisk.api.user.VerificationService;
import com.dy.networkdisk.user.config.Const;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationServiceImpl implements VerificationService {

    private final DefaultKaptcha kaptcha;
    private final StringRedisTemplate template;

    public byte[] getVerificationCode(String token){
        String code = kaptcha.createText();
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + ":" + token;
        template.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(kaptcha.createImage(code),"jpg",imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes.toByteArray();
    }
}
