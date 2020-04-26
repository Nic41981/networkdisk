package com.dy.networkdisk.api.dto.user

import java.io.Serializable

data class AutoLoginDTO(
        val id: Long,
        var ip: String,
        var ipLocation: String
):Serializable