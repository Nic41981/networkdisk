package com.dy.networkdisk.api.dto.user

import java.io.Serializable

data class ActiveDTO (
        var email: String = "", 
        var lock: String = "",
        var ip: String = "",
        var ipLocation: String = ""
): Serializable
