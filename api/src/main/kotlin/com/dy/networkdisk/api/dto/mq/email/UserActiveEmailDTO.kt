package com.dy.networkdisk.api.dto.mq.email;

import java.util.Date

data class UserActiveEmailDTO (
        var email: String = "",
        var nickname : String = "",
        var registerTime: Date = Date(),
        var registerIP: String = "",
        var registerIPLocation: String = "",
        var lock: String = "",
        var activeURL: String = ""
)
