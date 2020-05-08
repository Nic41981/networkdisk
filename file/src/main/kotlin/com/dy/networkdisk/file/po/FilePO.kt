package com.dy.networkdisk.file.po

import java.util.*

data class FilePO (
        val id: Long,
        val size: Long,
        val mime: String,
        val uploader: Long,
        val uploadTime: Date
)