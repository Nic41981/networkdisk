package com.dy.networkdisk.user.bo

import java.util.*

data class GuestsBO (
    var email: String = "",
    var nickname: String = "",
    var password: String = "",
    var inviterID: Long = 0L,
    var registerTime: Date = Date(),
    var registerIP: String = "",
    var registerIPLocation: String = "",
    var lock: String = ""
)