package com.dy.networkdisk.api.dto.file

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class FileNodeResult(
        var id: Long,
        val order: Int,
        var type: String,
        var name: String,
        var status: String,
        var size: String,
        var createTime: String
): Serializable