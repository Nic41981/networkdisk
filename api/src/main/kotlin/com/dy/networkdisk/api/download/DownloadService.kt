package com.dy.networkdisk.api.download

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.download.GetChunkIDListDTO

interface DownloadService {

    fun getChunkIDList(dto: GetChunkIDListDTO): QYResult<List<Long>>

    fun getChunk()
}