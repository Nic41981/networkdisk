package com.dy.networkdisk.api.storage

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.storage.BeforeUploadDTO
import com.dy.networkdisk.api.dto.storage.UploadDTO

interface UploadService {

    fun beforeUpload(dto: BeforeUploadDTO): QYResult<Long>

    fun upload(dto: UploadDTO)

}