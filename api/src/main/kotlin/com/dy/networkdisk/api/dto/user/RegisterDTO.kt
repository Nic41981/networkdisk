package com.dy.networkdisk.api.dto.user;

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable;

@NoArg
data class RegisterDTO(
        var email: String = "",
        var nickname: String = "",
        var password: String = "",
        var inviterID: Long = 0L,
        var ip: String = "",
        var ipLocation: String = "",
        var host: String = ""
): Serializable
