package com.dy.networkdisk.api.dto.user

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class LoginDTO (
        var email: String = "",
        var password: String = "",
        var ip: String = "",
        var ipLocation: String = ""
): Serializable
