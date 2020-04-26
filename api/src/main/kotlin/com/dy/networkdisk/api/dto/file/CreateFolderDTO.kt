package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class CreateFolderDTO(
        val sessionID: Long,
        val userID: Long,
        val parent: Long,
        val name: String
): Serializable