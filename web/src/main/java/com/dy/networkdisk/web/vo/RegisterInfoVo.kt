package com.dy.networkdisk.web.vo

data class RegisterInfoVo (
        var username : String = "",
        var password : String = "",
        var email : String = "",
        var verificationCode : String = ""
)