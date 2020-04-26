package com.dy.networkdisk.web.vo

class ResultJsonVO<T>(
        val status: Boolean,
        val msg: String = "",
        val data: T? = null
){
    companion object {
        fun <T> success(msg: String = "",data: T? = null): ResultJsonVO<T> {
            return ResultJsonVO(status = true, msg = msg, data = data)
        }

        fun <T> fail(msg: String,data: T? = null): ResultJsonVO<T> {
            return ResultJsonVO(status = false, msg = msg, data = data)
        }
    }
}