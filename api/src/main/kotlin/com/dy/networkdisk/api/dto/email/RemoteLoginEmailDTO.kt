package com.dy.networkdisk.api.dto.email

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class RemoteLoginEmailDTO(
        var email: String,
        var nickname: String,
        var lastLocation: String,
        var thisLocation: String,
        var loginIP: String
): Serializable