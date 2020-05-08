package com.dy.networkdisk.api.file

import com.dy.networkdisk.api.dto.QYResult

interface FileDownloadService {

    fun checkDownloadPermission(node: Long,owner: Long): QYResult<Unit>

}