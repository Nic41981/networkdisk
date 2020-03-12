package com.dy.networkdisk.web.tool;

import com.google.code.kaptcha.util.Config;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CachedConfig {

    private static final long CACHE_EXPIRE = 5*60*1000;
    private static final String KAPTCHA_CONFIG_KEY = "kaptchaConfig";

    private final StringRedisTemplate template;

    private Map<String,Object> cacheMap = new HashMap<>();
    private Map<String,Long> cacheExpireMap = new HashMap<>();

    public Config getKaptchaConfig(){
        Object configObj = cacheMap.get(KAPTCHA_CONFIG_KEY);
        if (configObj != null){
            long updateTimestamp = cacheExpireMap.getOrDefault(KAPTCHA_CONFIG_KEY,0L);
            if (System.currentTimeMillis() - updateTimestamp < CACHE_EXPIRE){
                if (configObj instanceof Config){
                    return (Config)configObj;
                }
            }
        }
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border",template.opsForValue().get("config::web::verification::border"));
        Config config = new Config(properties);
        cacheMap.put(KAPTCHA_CONFIG_KEY,config);
        cacheExpireMap.put(KAPTCHA_CONFIG_KEY,System.currentTimeMillis());
        return config;
    }

}
