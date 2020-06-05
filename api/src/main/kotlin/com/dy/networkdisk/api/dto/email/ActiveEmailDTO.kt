package com.dy.networkdisk.api.dto.email

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable
import java.util.*

@NoArg
data class ActiveEmailDTO(
        var email: String,
        var nickname : String,
        var registerTime: Date,
        var registerIP: String,
        var registerIPLocation: String,
        var lock: String,
        var activeURL: String
): Serializable