package com.dy.networkdisk.file.po

import com.dy.networkdisk.api.annotation.NoArg
import java.util.*

@NoArg
data class NodePO(
        var id: Long,
        var owner: Long,
        var parent: Long,
        var isFolder: Boolean,
        var file: Long? = null,
        var name: String,
        var status: String,
        var createTime: Date
)