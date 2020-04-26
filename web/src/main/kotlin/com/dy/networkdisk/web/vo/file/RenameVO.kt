package com.dy.networkdisk.web.vo.file

data class RenameVO(
        val parent: Long,
        val id: Long,
        val type: String,
        val name: String
)