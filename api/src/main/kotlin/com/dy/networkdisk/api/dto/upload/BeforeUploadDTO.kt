package com.dy.networkdisk.api.dto.upload

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class BeforeUploadDTO (
        val sessionID: Long,
        val userID: Long,
        val parent: Long,
        val name: String,
        val mime: String,
        val size: Long
):Serializable