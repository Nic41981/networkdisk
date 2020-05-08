package com.dy.networkdisk.api.upload

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.upload.BeforeUploadDTO
import com.dy.networkdisk.api.dto.upload.UploadDTO

interface UploadService {

    fun beforeUpload(dto: BeforeUploadDTO): QYResult<Long>

    fun upload(dto: UploadDTO)

}