package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class CreateUploadingFileDTO(
        val sessionID: Long,
        val userID: Long,
        val parent: Long,
        val name: String,
        val mime: String,
        val size: Long
): Serializable