package com.dy.networkdisk.api.dto.user

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class ActiveDTO (
        var email: String = "", 
        var lock: String = "",
        var ip: String = "",
        var ipLocation: String = ""
): Serializable
