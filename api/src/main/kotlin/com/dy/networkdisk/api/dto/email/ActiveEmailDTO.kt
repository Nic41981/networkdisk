package com.dy.networkdisk.api.dto.email

import java.util.*

data class ActiveEmailDTO(
        var email: String,
        var nickname : String,
        var registerTime: Date,
        var registerIP: String,
        var registerIPLocation: String,
        var lock: String,
        var activeURL: String
)