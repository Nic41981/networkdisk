package com.dy.networkdisk.admin.config

import com.dy.networkdisk.api.config.ConfigInfo
import kotlinx.coroutines.GlobalScope
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
        private val template: StringRedisTemplate
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val configFile = File(Const.CONFIG_PATH)
        val properties = Properties()
        try {
            if (!configFile.exists()) {
                throw Exception()
            }
            FileInputStream(configFile).use {
                properties.load(it)
            }
        } catch (e: Exception) {
            //文件不存在或读取异常
            println("配置文件读取失败,将使用默认配置启动!")
            properties.clear()
            for (it in ConfigInfo.values()) {
                properties.setProperty(it.propKey, it.default)
            }
            createFile(configFile,properties)
        }
        for (it in ConfigInfo.values()) {
            try {
                template.opsForValue()[it.redisKey] = properties.getProperty(it.propKey, it.default)
            } catch (e: Exception) {
                println("缺少Redis核心键值信息,请检查api模块版本!")
            }
        }
    }

    private fun createFile(configFile: File, properties: Properties){
        if (configFile.exists() && !configFile.delete()){
            println("旧配置文件删除失败")
            return
        }
        val parent = configFile.parentFile
        if (!parent.exists() && !parent.mkdirs()) {
            println("配置文件夹创建失败")
            return
        }
        if (!configFile.createNewFile()) {
            println("新配置文件创建失败")
            return
        }
        configFile.outputStream().use {
            properties.store(it,null)
        }
    }
}