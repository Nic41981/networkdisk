package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class CreateFileDTO(
        val sessionID: Long,
        val userID: Long,
        val parent: Long,
        val name: String,
        val size: Long
): Serializable