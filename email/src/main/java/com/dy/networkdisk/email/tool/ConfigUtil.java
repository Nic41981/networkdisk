package com.dy.networkdisk.email.tool;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConfigUtil {

    @Value("${QYDisk.config.cache.enable}")
    private boolean allowCache;
    @Value("${QYDisk.config.cache.expire}")
    private long cacheExpire;

    private ConcurrentHashMap<String,String> cacheMap;
    private ConcurrentHashMap<String,Long> cacheExpireMap;

    private StringRedisTemplate template;

    private static class SingletonHolder{
        private static ConfigUtil INSTANCE = new ConfigUtil();
    }

    private ConfigUtil(){
        this.cacheMap = new ConcurrentHashMap<>();
        this.cacheExpireMap = new ConcurrentHashMap<>();
    }

    @Autowired
    public ConfigUtil(StringRedisTemplate template){
        this();
        this.template = template;
    }

    @PostConstruct
    private void init(){
        SingletonHolder.INSTANCE.allowCache = this.allowCache;
        SingletonHolder.INSTANCE.cacheExpire = this.cacheExpire;
        SingletonHolder.INSTANCE.template = this.template;
    }

    //规避消息队列存在的问题
    public static ConfigUtil getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 检查缓存更新
     * @param key 所取值的redis键信息
     * @return 信息是否存在
     */
    public boolean checkExpire(ConfigRedisKey key){
        if (allowCache && cacheMap.containsKey(key.getKey())) {
            //允许缓存并且已有缓存,检查缓存时效
            Long updateTimestamp = cacheExpireMap.getOrDefault(key.getKey(), 0L);
            if (System.currentTimeMillis() - updateTimestamp < cacheExpire) {
                //有效期内,不做更新
                return true;
            }
        }
        //不允许缓存,无缓存或缓存失效,更新缓存
        String temp = template.opsForValue().get(key.getKey());
        if (StringUtil.isNull(temp)){
            //无配置信息
            return false;
        }
        cacheMap.put(key.getKey(),temp);
        cacheExpireMap.put(key.getKey(),System.currentTimeMillis());
        return true;
    }

    /**
     * 获取字符串参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    public String getString(@NonNull ConfigRedisKey key, @Nullable String defaultValue){
        boolean exist = checkExpire(key);
        if (!exist){
            log.warn("未找到参数:" + key.getKey());
            return defaultValue;
        }
        return cacheMap.get(key.getKey());
    }

    /**
     * 获取Integer类型参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    public Integer getInteger(@NonNull ConfigRedisKey key,@Nullable Integer defaultValue){
        String temp = getString(key,null);
        if (StringUtil.isNull(temp)){
            return defaultValue;
        }
        try{
            return Integer.parseInt(temp);
        } catch (Exception e){
            log.warn("参数" + key.getKey() + "不是Integer类型:" + temp);
        }
        return defaultValue;
    }

    /**
     * 获取Long类型参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    public Long getLong(@NonNull ConfigRedisKey key,@Nullable Long defaultValue){
        String temp = getString(key,null);
        if (StringUtil.isNull(temp)){
            return defaultValue;
        }
        try{
            return Long.parseLong(temp);
        } catch (Exception e){
            log.warn("参数" + key.getKey() + "不是Long类型:" + temp);
        }
        return defaultValue;
    }

    /**
     * 获取Boolean类型参数
     * 优化:避免错误配置导致致命bug(错误解析为false)
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    public Boolean getBoolean(@NonNull ConfigRedisKey key,@Nullable Boolean defaultValue){
        String temp = getString(key,null);
        if (StringUtil.isNull(temp)){
            return defaultValue;
        }
        switch (temp.toLowerCase()){
            case "yes":
            case "true":{
                return Boolean.TRUE;
            }
            case "no":
            case "false":{
                return Boolean.FALSE;
            }
            default:{
                log.warn("参数" +key.getKey() + "不是Boolean类型:" + temp);
            }
        }
        return defaultValue;
    }
}
