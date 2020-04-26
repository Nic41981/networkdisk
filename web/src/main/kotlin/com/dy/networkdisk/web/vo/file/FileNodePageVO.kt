package com.dy.networkdisk.web.vo.file

data class FileNodePageVO(
        val id: Long,
        val order: Int,
        val name: String,
        val status: String,
        val size: String,
        val createTime: String
)