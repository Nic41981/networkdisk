package com.dy.networkdisk.storage.po

import java.util.*

data class UploadPO(
        val id: Long,
        val file: Long,
        val chunk: Long,
        val sequence: Int,
        val uploader: Long,
        val uploadTime: Date
)