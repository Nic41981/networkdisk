package com.dy.networkdisk.file.service

import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.file.FileDownloadService
import com.dy.networkdisk.file.dao.NodeMapper
import org.springframework.stereotype.Service
import org.apache.dubbo.config.annotation.Service as DubboService
import javax.annotation.Resource

@Service
@DubboService
class FileDownloadServiceImpl: FileDownloadService {

    @Resource
    private lateinit var nodeMapper: NodeMapper

    override fun checkDownloadPermission(node: Long, owner: Long): QYResult<Unit> {
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
        return QYResult.success()
    }

}