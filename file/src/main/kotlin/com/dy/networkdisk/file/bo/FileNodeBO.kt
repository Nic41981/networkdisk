package com.dy.networkdisk.file.bo

import java.util.*

data class FileNodeBO (
        var id: Long,
        var name: String,
        var mimeType: String,
        var status: String,
        var size: Long,
        var createTime: Date
)