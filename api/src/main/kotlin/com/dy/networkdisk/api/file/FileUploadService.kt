package com.dy.networkdisk.api.file

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.CreateUploadingFileDTO

interface FileUploadService {

    fun createUploadingFile(dto: CreateUploadingFileDTO): QYResult<Pair<Long, Long>>

    fun onUploadFinish(nodeID: Long, result: Boolean)
}