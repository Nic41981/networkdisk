package com.dy.networkdisk.api.dto.user

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class AutoLoginDTO(
        val id: Long,
        var ip: String,
        var ipLocation: String
):Serializable