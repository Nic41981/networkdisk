package com.dy.networkdisk.web.vo

data class RegisterVO (
        var email: String = "",
        var nickname: String = "",
        var password: String = "",
        var invitationCode: String = "",
        var verificationCode: String = "",
        var host: String = ""
)