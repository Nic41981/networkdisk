package com.dy.networkdisk.file.tool

import java.util.concurrent.TimeUnit

private const val TIME_OUT = 30L

fun Any.getLock(name: String,value: String = ""): Boolean{
    val ops = RedisUtils.getValueOps()
    return ops.setIfAbsent(name,value, TIME_OUT,TimeUnit.SECONDS) ?: false
}

fun Any.removeLock(name: String){
    val template = RedisUtils.getInstance()
    template.delete(name)
}