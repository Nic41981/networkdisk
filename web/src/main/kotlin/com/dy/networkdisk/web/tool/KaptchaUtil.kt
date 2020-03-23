package com.dy.networkdisk.web.tool

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.config.KaptchaKeyInfo
import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class KaptchaUtil{
    private val random = SecureRandom()

    @Autowired
    private lateinit var template: StringRedisTemplate

    @Autowired
    private lateinit var config: ConfigUtil

    private val kaptcha = DefaultKaptcha()

    /**
     * 产生随机字符串(优化)并存入redis
     * @param token 用户会话信息
     * @return 验证码字符串
     */
    fun createText(token: String): String {
        val charset = config.getString(ConfigRedisKey.WEB_VERIFICATION_CHARSET, Const.VERIFICATION_CHINESE)
        val length = config.getInteger(ConfigRedisKey.WEB_VERIFICATION_LENGTH, 2)
        val expire = config.getLong(ConfigRedisKey.WEB_VERIFICATION_EXPIRE, 5L)
        val buff = StringBuilder(length)
        for (i in 1..length) {
            buff.append(charset[random.nextInt(charset.length)])
        }
        val answer = buff.toString()
        val key = "${Const.FUNC_VERIFICATION_REDIS_KEY}:${token}"
        template.opsForValue().set(key,answer,expire,TimeUnit.MINUTES)
        return answer
    }

    /**
     * 根据文字获取验证码图片
     * @param text 验证码字符串
     * @return 验证码图片
     */
    fun createImage(text: String): BufferedImage {
        return getKaptcha().createImage(text)
    }

    /**
     * 判断验证码是否正确
     * @param token 用户会话信息
     * @param code 用户输入验证码
     * @return 判断结果
     */
    fun check(token: String, code: String): Boolean {
        val ignoreCase = config.getBoolean(ConfigRedisKey.WEB_VERIFICATION_CASE_IGNORE, false)
        val key = "${Const.FUNC_VERIFICATION_REDIS_KEY}:${token}"
        val answer = template.opsForValue()[key] ?: return false
        return answer.equals(code,ignoreCase)
    }

    /**
     * 组装Kaptcha属性并返回Kaptcha
     * @return Kaptcha对象
     */
    private fun getKaptcha(): DefaultKaptcha {
        val properties = Properties()
        for (it in KaptchaKeyInfo.values()) {
            val value = config.getString(it.redisKey, "")
            if (value.isNotBlank()) {
                properties.setProperty(it.kaptchaKey, value)
            }
        }
        properties.setProperty("kaptcha.image.width", "100")
        properties.setProperty("kaptcha.image.height", "30")
        kaptcha.config = Config(properties)
        return kaptcha
    }
}