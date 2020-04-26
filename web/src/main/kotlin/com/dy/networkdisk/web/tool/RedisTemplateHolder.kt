package com.dy.networkdisk.web.tool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisTemplateHolder @Autowired constructor(
        template: StringRedisTemplate
){
    companion object{
        lateinit var INSTANCE: StringRedisTemplate
    }

    init {
        INSTANCE = template
    }
}