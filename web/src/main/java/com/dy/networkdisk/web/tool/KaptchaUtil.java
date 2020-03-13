package com.dy.networkdisk.web.tool;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.config.KaptchaKeyInfo;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KaptchaUtil {
    private final static SecureRandom R = new SecureRandom();

    private StringRedisTemplate template;

    @Value("${QYDisk.config.cache.enable}")
    private boolean allowCache;

    @Value("${QYDisk.config.cache.expire}")
    private long cacheExpire;


    private DefaultKaptcha kaptcha;
    private Properties properties;
    private long cachedTimestamp;

    private static class Setting{
        private static String charset;
        private static int length;
        private static boolean ignoreCase;
        private static long expire;
    }

    @Autowired
    public KaptchaUtil(StringRedisTemplate template){
        this.template = template;
        this.properties = new Properties();
        this.kaptcha = new DefaultKaptcha();
        //固定设置
        properties.setProperty("kaptcha.image.width", "70");
        properties.setProperty("kaptcha.image.height", "25");
        load();
    }

    /**
     * 产生随机字符串(优化)并存入redis
     * @param token 用户会话信息
     * @return 验证码字符串
     */
    public String createText(String token){
        checkExpire();
        StringBuilder buff = new StringBuilder(Setting.length);
        for (int i = 0; i < Setting.length; i++){
            buff.append(Setting.charset.charAt(R.nextInt(Setting.charset.length())));
        }
        String answer = buff.toString();
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + "::" + token;
        template.opsForValue().set(key,answer,Setting.expire,TimeUnit.MINUTES);
        return answer;
    }

    /**
     * 根据文字获取验证码图片
     * @param text 验证码字符串
     * @return 验证码图片
     */
    public BufferedImage createImage(String text){
        checkExpire();
        return kaptcha.createImage(text);
    }

    /**
     * 判断验证码是否正确
     * @param token 用户会话信息
     * @param code 用户输入验证码
     * @return 判断结果
     */
    public boolean check(String token,String code){
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + "::" + token;
        String answer = template.opsForValue().get(key);
        if (answer == null){
            return false;
        }
        if (Setting.ignoreCase){
            return answer.equalsIgnoreCase(code);
        }
        return answer.equals(code);
    }

    /**
     * 判断缓存过期,过期则更新缓存
     */
    private void checkExpire(){
        if (!allowCache || System.currentTimeMillis() - cachedTimestamp > cacheExpire){
            load();
        }
    }

    /**
     * 更新缓存
     */
    private synchronized void load(){
        //加载验证码字符集
        String temp = template.opsForValue().get(ConfigRedisKey.WEB_VERIFICATION_CHARSET);
        if (temp == null || temp.length() == 0){
            log.warn("验证码字符集设置错误,默认值:预设中文字符集");
            Setting.charset = Const.VERIFICATION_CHINESE;
        } else {
            Setting.charset = temp;
        }
        //加载验证码长度
        temp = template.opsForValue().get(ConfigRedisKey.WEB_VERIFICATION_LENGTH);
        try {
            if (temp == null || temp.length() == 0){
                throw new Exception();
            }
            Setting.length = Integer.parseInt(temp);
        }catch (Exception e){
            log.warn("验证码长度设置错误,默认值:2");
            Setting.length = 2;
        }
        //加载忽略大小写设置
        Setting.ignoreCase = Boolean.parseBoolean(template.opsForValue().get(ConfigRedisKey.WEB_VERIFICATION_CASE_IGNORE));
        //加载验证码过期时长
        temp = template.opsForValue().get(ConfigRedisKey.WEB_VERIFICATION_EXPIRE);
        try {
            if (temp == null || temp.length() == 0){
                throw new Exception();
            }
            Setting.expire = Long.parseLong(temp);
        } catch (Exception e){
            log.warn("验证码有效期设置错误,默认值:5");
            Setting.expire = 5;
        }
        //加载其他kaptcha设置
        for (KaptchaKeyInfo it : KaptchaKeyInfo.values()){
            String value = template.opsForValue().get(it.getRedisKey().getKey());
            if (value == null){
                log.warn("redis中未找到kaptcha属性:" + it.getKaptchaKey());
                continue;
            }
            properties.setProperty(it.getKaptchaKey(),value);
        }
        kaptcha.setConfig(new Config(properties));
        //更新缓存时间
        cachedTimestamp = System.currentTimeMillis();
    }
}
