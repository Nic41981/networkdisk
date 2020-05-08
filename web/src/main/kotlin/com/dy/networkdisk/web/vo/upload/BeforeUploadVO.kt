package com.dy.networkdisk.web.vo.upload

data class BeforeUploadVO (
        val parent: Long,
        val name: String,
        val mime: String,
        val size: Long
)