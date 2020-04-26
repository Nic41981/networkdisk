package com.dy.networkdisk.web.vo.upload

import org.springframework.web.multipart.MultipartFile

data class FileUploadVO(
        val id: String,
        val parent: Long,
        val name: String,
        val md5: String,
        val size: Long,
        val chunks: Int,
        val chunk: Int,
        val file: MultipartFile
)