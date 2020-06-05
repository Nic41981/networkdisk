package com.dy.networkdisk.web.vo.file

data class FolderTreeJsonVO(
        val id: String,
        val text: String,
        val state: String = "closed"
)