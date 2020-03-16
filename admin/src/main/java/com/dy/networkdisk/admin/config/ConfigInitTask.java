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

    /**
     * 初始化配置信息
     * @param args 运行参数
     */
    @Override
    public void run(ApplicationArguments args) {
        File configFile = new File(Const.CONFIG_PATH);
        boolean hasConfig = checkConfigFile(configFile);
        Properties properties = new Properties();
        if (hasConfig) {
            //存在配置文件
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (Exception e) {
                log.warn("配置文件读取失败,将使用默认配置启动!");
            }
        }
        for (ConfigPropInfo it : ConfigPropInfo.values()){
            try {
                String value = properties.getProperty(it.getKey(), it.getDefaultValue());
                String key = ConfigRedisKey.valueOf(it.name()).getKey();
                template.opsForValue().set(key, value);
            } catch (Exception e){
                log.error("缺少Redis核心键值信息,请检查api模块版本!");
            }
        }
    }

    /**
     * 判断配置文件是否存在,若不存在则尝试创建默认配置文件
     * @param configFile 配置文件信息
     * @return 配置文件是否存在
     */
    private boolean checkConfigFile(File configFile){
        if (configFile.exists()){
            return true;
        }
        try {
            File parent = configFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()){
                throw new Exception("配置文件夹创建失败");
            }
            if (!configFile.createNewFile()){
                throw new Exception("配置文件创建失败");
            }
            Properties properties = new Properties();
            for (ConfigPropInfo it : ConfigPropInfo.values()){
                properties.setProperty(it.getKey(),it.getDefaultValue());
            }
            properties.store(new FileOutputStream(configFile),null);
        }catch (Exception e){
            log.warn("默认配置文件创建失败!",e);
        }
        return false;
    }
}
