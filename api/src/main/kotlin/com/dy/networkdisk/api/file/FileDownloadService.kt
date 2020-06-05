package com.dy.networkdisk.api.file

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.FileInfoDTO

interface FileDownloadService {

    fun checkFileInfo(node: Long, owner: Long): QYResult<FileInfoDTO>

}