package com.dy.networkdisk.web.vo

import kotlin.collections.ArrayList

data class MessagePageVO(
        var type : String
){
    var content : ArrayList<String> = arrayListOf()
    var willJump : Boolean = false
    var jumpString : String = ""
    var jumpURL : String = ""

    fun jump(str: String,url: String){
        this.willJump = true
        this.jumpString = str
        this.jumpURL = url
    }
}