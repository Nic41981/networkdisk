package com.dy.networkdisk.api.dto.user

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class LoginResult(
        val id: Long,
        val email: String,
        val nickname: String,
        val type: String
): Serializable