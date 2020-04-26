package com.dy.networkdisk.web.vo

class ResultDialogVO(
        val title: String,
        val type: String,
        val content: String
){
    companion object{
        const val NAME = "result"
        const val TYPE_INFO = "info"
        const val TYPE_QUESTION = "question"
        const val TYPE_WARNING = "warning"
        const val TYPE_ERROR = "error"
    }
}