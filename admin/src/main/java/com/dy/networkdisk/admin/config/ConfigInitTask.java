package com.dy.networkdisk.admin.config;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigInitTask implements ApplicationRunner {

    private final StringRedisTemplate template;

    @Override
    public void run(ApplicationArguments args) {
        HashMap<String,String> configMap = new HashMap<>();
        for (ConfigPropInfo it : ConfigPropInfo.values()){
            //初始化默认配置
            try {
                ConfigRedisKey redisKey = ConfigRedisKey.valueOf(it.name());
                configMap.put(redisKey.getKey(),it.getDefaultValue());
            }catch (Exception e){
                log.error("核心Redis键值信息缺失,请检查api模块版本是否正确",e);
            }
        }
        try {
            File configFile = new File(Const.CONFIG_PATH);
            if (!configFile.exists()) {
                //未找到配置文件，创建默认配置文件后中断配置更新
                createDefaultConfigFile(configFile);
                throw new Exception("未找到配置文件,将使用默认配置启动!");
            }
            Properties properties = new Properties();
            try(FileInputStream fis = new FileInputStream(configFile)){
                properties.load(fis);
            } catch (Exception e){
                //配置文件读取失败，中断配置更新
                throw new Exception("配置文件读取失败,将使用默认配置启动!");
            }
            for (String key : properties.stringPropertyNames()){
                //更新用户配置信息
                try{
                    ConfigRedisKey redisKey = ConfigRedisKey.valueOf(key);
                    configMap.put(redisKey.getKey(),properties.getProperty(key));
                } catch (Exception e){

                    log.warn("忽略未知设置:" + key);
                }
            }
        } catch (Exception e){
            log.warn(e.getMessage(),e);
        } finally {
            loadUserConst(configMap);
        }
    }

    private void createDefaultConfigFile(File configFile){
        try {
            if (!configFile.createNewFile()){
                throw new Exception();
            }
            Properties properties = new Properties();
            for (ConfigPropInfo it : ConfigPropInfo.values()){
                properties.setProperty(it.getKey(),it.getDefaultValue());
            }
            properties.store(new FileOutputStream(configFile),null);
        }catch (Exception e){
            log.warn("默认配置文件创建失败!",e);
        }
    }

    private void loadUserConst(Map<String,String> configMap){
        for (String key : configMap.keySet()){
            template.opsForValue().set(key,configMap.get(key));
        }
    }
}
