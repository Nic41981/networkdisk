package com.dy.networkdisk.web.vo.user

data class LoginVO (
        var email: String = "",
        var password: String = "",
        var verificationCode: String = "",
        var autoLogin: Boolean = false
)