package com.dy.networkdisk.api.storage

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.download.GetChunkIDListDTO
import com.dy.networkdisk.api.dto.storage.DownloadInfoResult

interface DownloadService {

    fun getChunkIDList(dto: GetChunkIDListDTO): QYResult<DownloadInfoResult>

    fun getChunk(id: Long): QYResult<ByteArray>
}