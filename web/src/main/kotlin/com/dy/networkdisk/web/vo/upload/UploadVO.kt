package com.dy.networkdisk.web.vo.upload

import org.springframework.web.multipart.MultipartFile

data class UploadVO(
        val task: Long,
        var chunks: Int = 1,
        var chunk: Int = 1,
        val size: Long,
        val file: MultipartFile
)