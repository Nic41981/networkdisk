package com.dy.networkdisk.api.dto

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
class QYResult<T> private constructor(
        val isSuccess: Boolean,
        val msg: String,
        val data: T?
): Serializable{
    companion object{
        fun <T> success(msg: String = "",data: T? = null): QYResult<T> {
            return QYResult(isSuccess = true, msg = msg, data = data)
        }

        fun <T> fail(msg: String,data: T? = null): QYResult<T> {
            return QYResult(isSuccess = false, msg = msg, data = data)
        }
    }
}