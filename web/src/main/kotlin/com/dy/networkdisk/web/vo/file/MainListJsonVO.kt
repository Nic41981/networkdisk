package com.dy.networkdisk.web.vo.file

data class MainListJsonVO(
        val id: String,
        val type: String,
        val order: Int,
        val name: String,
        val status: String,
        val size: String,
        val createTime: String
)