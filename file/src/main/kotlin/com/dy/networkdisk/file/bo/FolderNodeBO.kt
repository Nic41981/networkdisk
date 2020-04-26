package com.dy.networkdisk.file.bo

import java.util.*

data class FolderNodeBO(
        val id: Long,
        val name: String,
        val mineType: String? = null,
        val createTime: Date
)