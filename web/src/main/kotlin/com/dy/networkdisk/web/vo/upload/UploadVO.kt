package com.dy.networkdisk.web.vo.upload

import org.springframework.web.multipart.MultipartFile

data class UploadVO(
        val QYUploadID: Long,
        val chunks: Int,
        val chunk: Int,
        val size: Long,
        val file: MultipartFile
)