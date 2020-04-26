package com.dy.networkdisk.file.po

import com.dy.networkdisk.api.annotation.NoArg
import java.util.*

@NoArg
data class FileNodePO(
        var id: Long,
        var userID: Long,
        var parent: Long,
        var nodeType: String,
        var mimeType: String? = null,
        var name: String,
        var status: String,
        var size: Long? = null,
        var createTime: Date
)