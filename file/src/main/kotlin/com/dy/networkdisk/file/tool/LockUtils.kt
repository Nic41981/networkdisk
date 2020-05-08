package com.dy.networkdisk.file.tool

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

private const val TIME_OUT = 30L

fun Any.getLock(name: String,value: String = ""): Boolean{
    val ops = RedisUtils.getValueOps()
    return ops.setIfAbsent(name,value, TIME_OUT,TimeUnit.SECONDS) ?: false
}

fun Any.getLockWithDelay(name: String,value: String = "",retry: Int = 1): Boolean{
    var count = 0
    while (count < retry){
        if (getLock(name,value)){
            return true
        }
        runBlocking {
            count ++
            delay(5000)
        }
    }
    return false
}

fun Any.removeLock(name: String){
    val template = RedisUtils.getInstance()
    template.delete(name)
}