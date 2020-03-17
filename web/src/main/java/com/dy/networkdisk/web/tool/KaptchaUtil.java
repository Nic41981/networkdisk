package com.dy.networkdisk.web.tool;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.config.KaptchaKeyInfo;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KaptchaUtil {
    private final static SecureRandom R = new SecureRandom();

    private final StringRedisTemplate template;
    private final ConfigUtil config;

    private DefaultKaptcha kaptcha = new DefaultKaptcha();

    /**
     * 产生随机字符串(优化)并存入redis
     * @param token 用户会话信息
     * @return 验证码字符串
     */
    public String createText(String token){
        String charset = config.getString(ConfigRedisKey.WEB_VERIFICATION_CHARSET,Const.VERIFICATION_CHINESE);
        Integer length = config.getInteger(ConfigRedisKey.WEB_VERIFICATION_LENGTH,2);
        Long expire = config.getLong(ConfigRedisKey.WEB_VERIFICATION_EXPIRE,5L);
        StringBuilder buff = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            buff.append(charset.charAt(R.nextInt(charset.length())));
        }
        String answer = buff.toString();
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + "::" + token;
        template.opsForValue().set(key,answer,expire,TimeUnit.MINUTES);
        return answer;
    }

    /**
     * 根据文字获取验证码图片
     * @param text 验证码字符串
     * @return 验证码图片
     */
    public BufferedImage createImage(String text){
        return getKaptcha().createImage(text);
    }

    /**
     * 判断验证码是否正确
     * @param token 用户会话信息
     * @param code 用户输入验证码
     * @return 判断结果
     */
    public boolean check(String token,String code){
        Boolean ignoreCase = config.getBoolean(ConfigRedisKey.WEB_VERIFICATION_CASE_IGNORE,false);
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + "::" + token;
        String answer = template.opsForValue().get(key);
        if (answer == null){
            return false;
        }
        if (ignoreCase){
            return answer.equalsIgnoreCase(code);
        }
        return answer.equals(code);
    }

    /**
     * 组装Kaptcha属性并返回Kaptcha
     * @return Kaptcha对象
     */
    private DefaultKaptcha getKaptcha(){
        Properties properties = new Properties();
        for (KaptchaKeyInfo it : KaptchaKeyInfo.values()){
            String value = config.getString(it.getRedisKey(),null);
            if (value == null){
                log.warn("redis中未找到kaptcha属性:" + it.getKaptchaKey());
                continue;
            }
            properties.setProperty(it.getKaptchaKey(),value);
        }
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "30");
        kaptcha.setConfig(new Config(properties));
        return kaptcha;
    }
}
