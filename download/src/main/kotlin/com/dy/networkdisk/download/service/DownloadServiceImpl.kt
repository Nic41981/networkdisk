package com.dy.networkdisk.download.service

import com.dy.networkdisk.api.download.DownloadService
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.download.GetChunkIDListDTO
import com.dy.networkdisk.api.file.FileDownloadService
import org.apache.dubbo.config.annotation.Reference
import org.springframework.stereotype.Service
import org.apache.dubbo.config.annotation.Service as DubboService


@Service
@DubboService
class DownloadServiceImpl: DownloadService {

    @Reference
    private lateinit var fileService: FileDownloadService

    override fun getChunkIDList(dto: GetChunkIDListDTO): QYResult<List<Long>> {
        val result = fileService.checkDownloadPermission(dto.nodeID,dto.owner)
        if (result.isSuccess){

        }
        else {
            return QYResult.fail(msg = result.msg)
        }
        TODO("Not yet implemented")
    }

    override fun getChunk() {
        TODO("Not yet implemented")
    }
}