package com.dy.networkdisk.web.vo.upload

data class BeforeUploadVO (
        val parent: String,
        val name: String,
        val mime: String,
        val size: Long
)