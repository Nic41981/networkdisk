package com.dy.networkdisk.storage.tool

import com.dy.networkdisk.api.config.ConfigInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ConfigUtil {

    @Value("\${QYDisk.config.cache.enable}")
    private val allowCache = false

    @Value("\${QYDisk.config.cache.expire}")
    private val cacheExpire: Long = 0

    @Autowired
    private lateinit var template: StringRedisTemplate

    private var cacheMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    private var cacheExpireMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    /**
     * 检查缓存更新
     * @param key 所取值的redis键信息
     * @return 信息是否存在
     */
    private fun checkCache(key: ConfigInfo): Boolean {
        if (allowCache && cacheMap.containsKey(key.redisKey)) {
            //允许缓存并且已有缓存,检查缓存时效
            val updateTimestamp = cacheExpireMap.getOrDefault(key.redisKey, 0L)
            if (System.currentTimeMillis() - updateTimestamp < cacheExpire) {
                //有效期内,不做更新
                return true
            }
        }
        //不允许缓存,无缓存或缓存失效,更新缓存
        val temp = template.opsForValue()[key.redisKey]
        if (temp?.isNotBlank() == true) {
            cacheMap[key.redisKey] = temp
            cacheExpireMap[key.redisKey] = System.currentTimeMillis()
            return true
        }
        return false
    }

    /**
     * 获取字符串参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    fun getString(key: ConfigInfo, defaultValue: String): String {
        if (checkCache(key)){
            val temp = cacheMap[key.redisKey]
            if (temp?.isNotBlank() == true){
                return temp
            }
        }
        return defaultValue
    }

    /**
     * 获取Integer类型参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    fun getInteger(key: ConfigInfo, defaultValue: Int): Int {
        val temp = getString(key, "")
        if (temp.isNotBlank()){
            return try {
                temp.toInt()
            } catch (e: Exception) {
                defaultValue
            }
        }
        return defaultValue
    }

    /**
     * 获取Long类型参数
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    fun getLong(key: ConfigInfo, defaultValue: Long): Long {
        val temp = getString(key, "")
        if (temp.isNotBlank()){
            return try {
                temp.toLong()
            } catch (e: Exception) {
                defaultValue
            }
        }
        return defaultValue
    }

    /**
     * 获取Boolean类型参数
     * 优化:避免错误配置导致致命bug(错误解析为false)
     * @param key redis键信息
     * @param defaultValue 默认值
     * @return 参数信息
     */
    fun getBoolean(key: ConfigInfo, defaultValue: Boolean): Boolean {
        val temp = getString(key, "")
        return when (temp.toLowerCase()) {
            "yes", "true" -> {
                true
            }
            "no", "false" -> {
                false
            }
            else -> {
                defaultValue
            }
        }
    }
}