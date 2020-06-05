package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class FileInfoDTO (
        val id: Long,
        val name: String,
        val size: Long,
        val mime: String
): Serializable