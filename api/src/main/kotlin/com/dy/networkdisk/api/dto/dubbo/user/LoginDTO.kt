package com.dy.networkdisk.api.dto.dubbo.user

import java.io.Serializable

data class LoginDTO (
        var email: String,
        var password: String,
        var ip: String, 
        var ipLocation: String
): Serializable
