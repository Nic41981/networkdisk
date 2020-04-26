package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class DeleteDTO (
        val sessionID: Long,
        val userID: Long,
        val parent: Long,
        val id: Long,
        val type: String
): Serializable