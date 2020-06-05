package com.dy.networkdisk.storage.po

import java.util.*

data class DownloadPO (
        val id: Long,
        val node: Long,
        val isShare: Boolean,
        val shareTime: Date? = null,
        val downloadTime: Date
)