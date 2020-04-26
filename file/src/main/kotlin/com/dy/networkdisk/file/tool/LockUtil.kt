//package com.dy.networkdisk.file.tool
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.redis.core.StringRedisTemplate
//import org.springframework.stereotype.Component
//import java.util.concurrent.TimeUnit
//
//@Component
//class LockUtil @Autowired constructor(
//        private val template: StringRedisTemplate
//) {
//
//    fun getLock(
//            name: String,
//            value: String = "",
//            expire: Long = 1,
//            timeUnit: TimeUnit = TimeUnit.MINUTES
//    ): Boolean{
//        return template.opsForValue().setIfAbsent(name,value,expire,timeUnit) ?: false
//    }
//
//    fun removeLock(name: String){
//        template.delete(name)
//    }
//}