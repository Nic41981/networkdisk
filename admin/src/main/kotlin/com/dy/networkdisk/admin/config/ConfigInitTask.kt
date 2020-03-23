package com.dy.networkdisk.admin.config

import com.dy.networkdisk.api.config.ConfigRedisKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

@Configuration
open class ConfigInitTask @Autowired constructor(
        val template: StringRedisTemplate
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val configFile = File(Const.CONFIG_PATH)
        val hasConfig: Boolean = checkConfigFile(configFile)
        val properties = Properties()
        if (hasConfig) {
            //存在配置文件
            try {
                FileInputStream(configFile).use { fis -> properties.load(fis) }
            } catch (e: Exception) {
                println("配置文件读取失败,将使用默认配置启动!")
            }
        }
        for (it in ConfigPropInfo.values()) {
            try {
                val value = properties.getProperty(it.key, it.default)
                val key = ConfigRedisKey.valueOf(it.name).key
                template.opsForValue()[key] = value
            } catch (e: Exception) {
                println("缺少Redis核心键值信息,请检查api模块版本!")
            }
        }
    }

    /**
     * 判断配置文件是否存在,若不存在则尝试创建默认配置文件
     * @param configFile 配置文件信息
     * @return 配置文件是否存在
     */
    private fun checkConfigFile(configFile: File): Boolean {
        if (configFile.exists()) {
            return true
        }
        try {
            val parent = configFile.parentFile
            if (!parent.exists() && !parent.mkdirs()) {
                throw Exception("配置文件夹创建失败")
            }
            if (!configFile.createNewFile()) {
                throw Exception("配置文件创建失败")
            }
            val properties = Properties()
            for (it in ConfigPropInfo.values()) {
                properties.setProperty(it.key, it.default)
            }
            properties.store(FileOutputStream(configFile), null)
        } catch (e: Exception) {
            println("默认配置文件创建失败!")
        }
        return false
    }
}