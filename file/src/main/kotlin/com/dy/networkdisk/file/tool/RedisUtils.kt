package com.dy.networkdisk.file.tool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component

@Component
class RedisUtils @Autowired constructor(
        template: StringRedisTemplate
) {

    private object Holder {
        lateinit var INSTANCE: StringRedisTemplate
    }

    init {
        Holder.INSTANCE = template
    }

    companion object {
        fun getInstance(): StringRedisTemplate{
            return Holder.INSTANCE
        }

        fun getValueOps(): ValueOperations<String,String> {
            return getInstance().opsForValue()
        }

        fun getHashOps(): HashOperations<String,String,String> {
            return getInstance().opsForHash()
        }
    }
}