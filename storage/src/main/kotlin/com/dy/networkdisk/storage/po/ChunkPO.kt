package com.dy.networkdisk.storage.po

import java.util.*

data class ChunkPO(
        val id: Long,
        val size: Long,
        val md5: String,
        val sha256: String,
        val path: String,
        val uploader: Long,
        val uploadTime: Date
)