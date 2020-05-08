package com.dy.networkdisk.api.dto.upload

import com.dy.networkdisk.api.annotation.NoArg
import java.io.InputStream
import java.io.Serializable

@NoArg
data class UploadDTO(
        val id: Long,
        val chunks: Int,
        val chunk: Int,
        val size: Long,
        val file: InputStream
): Serializable