package com.dy.networkdisk.web.tool

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonUtil {
    companion object{
        private val gson = Gson()

        fun toJson(obj: Any): String{
            return gson.toJson(obj)
        }

        fun <T> fromJson(json: String,type: Type): T?{
            return try {
                gson.fromJson(json,type)
            } catch (e: Exception){
                null
            }
        }
    }
}