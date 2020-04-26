package com.dy.networkdisk.web.vo.file

data class FolderTreeJsonVO(
        val id: Long,
        val text: String,
        val state: String = "closed"
)