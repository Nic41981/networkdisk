package com.dy.networkdisk.file.tool

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

fun Any?.toJson(): String{
    this ?: return "{}"
    return gson.toJson(this)
}

inline fun <reified T> String?.fromJson(): T?{
    if (this.isNullOrBlank()){
        return null
    }
    return try {
        val type = object : TypeToken<T>(){}.type
        gson.fromJson(this,type)
    } catch (e: Exception){
        null
    }
}