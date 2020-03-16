package com.dy.networkdisk.web.vo

data class RegisterInfoVo (
        var username : String = "",
        var password : String = "",
        var confirmPassword : String = "",
        var email : String = "",
        var verificationCode : String = ""
)