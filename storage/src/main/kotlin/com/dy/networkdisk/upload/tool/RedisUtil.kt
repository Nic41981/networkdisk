package com.dy.networkdisk.upload.tool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component

@Component
class RedisUtil @Autowired constructor(
        template: StringRedisTemplate
) {

    companion object {
        val valueOps: ValueOperations<String,String> by lazy {
            Holder.INSTANCE.opsForValue()
        }

        val hashOps: HashOperations<String,String,String> by lazy {
            Holder.INSTANCE.opsForHash()
        }
    }


    object Holder{
        lateinit var INSTANCE: StringRedisTemplate
    }

    init {
        Holder.INSTANCE = template
    }
}

val Any?.template: StringRedisTemplate
    get() = RedisUtil.Holder.INSTANCE

val Any?.valueOps: ValueOperations<String,String>
    get() = RedisUtil.valueOps

val Any?.hashOps: HashOperations<String,String,String>
    get() = RedisUtil.hashOps