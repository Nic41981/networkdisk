package com.dy.networkdisk.file.service

import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.FileInfoDTO
import com.dy.networkdisk.api.file.FileDownloadService
import com.dy.networkdisk.file.dao.FileMapper
import com.dy.networkdisk.file.dao.NodeMapper
import org.springframework.stereotype.Service
import org.apache.dubbo.config.annotation.Service as DubboService
import javax.annotation.Resource

@Service
@DubboService
class FileDownloadServiceImpl: FileDownloadService {

    @Resource
    private lateinit var nodeMapper: NodeMapper

    @Resource
    private lateinit var fileMapper: FileMapper

    override fun checkFileInfo(node: Long, owner: Long): QYResult<FileInfoDTO> {
        val status = nodeMapper.checkPermission(
                id = node,
                parent = null,
                userID = owner,
                isFolder = false
        ) ?: return QYResult.fail(msg = "文件不存在")
        if (status == FileConst.Status.DELETE.name){
            return QYResult.fail(msg = "文件不存在")
        }
        if (status != FileConst.Status.NORMAL.name){
            return QYResult.fail(msg = "文件当前无法下载")
        }
        val fileID = nodeMapper.findFileIDByID(node)
        val file = fileMapper.findFileByID(fileID)
        return QYResult.success(data = FileInfoDTO(
                id = fileID,
                size = file.size,
                mime = file.mime
        ))
    }

}