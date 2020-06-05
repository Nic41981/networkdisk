package com.dy.networkdisk.file.service

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.CreateUploadingFileDTO
import com.dy.networkdisk.api.file.FileUploadService
import com.dy.networkdisk.file.bo.FileNodeBO
import com.dy.networkdisk.file.config.Const
import com.dy.networkdisk.file.dao.FileMapper
import com.dy.networkdisk.file.dao.NodeMapper
import com.dy.networkdisk.file.po.FilePO
import com.dy.networkdisk.file.po.NodePO
import com.dy.networkdisk.file.tool.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import org.apache.dubbo.config.annotation.Service as DubboService
import javax.annotation.Resource

@Service
@DubboService
class FileUploadServiceImpl @Autowired constructor(
        val idWorker: IDWorker
): FileUploadService {

    private val hashOps = RedisUtils.getHashOps()

    @Resource
    private lateinit var nodeMapper: NodeMapper

    @Resource
    private lateinit var fileMapper: FileMapper

    override fun createUploadingFile(dto: CreateUploadingFileDTO): QYResult<Pair<Long,Long>> {
        val lockName = "${Const.LOCK_FILE_NAME}:${dto.userID}:${dto.parent}"
        if (!getLockWithDelay(name = lockName,retry = 3)){
            return QYResult.fail(msg = "您的操作太频繁")
        }
        try {
            //权限检查
            val parentStatus = nodeMapper.checkPermission(
                    id = dto.parent,
                    parent = null,
                    userID = dto.userID,
                    isFolder = true
            )
            if (parentStatus != FileConst.Status.NORMAL.name){
                return QYResult.fail(msg = "目录状态不可用")
            }
            //重名检查
            val hasSaneName = nodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    isFolder = false,
                    name = dto.name
            )
            if (hasSaneName != 0){
                return QYResult.fail(msg = "存在同名文件")
            }
            //创建文件信息
            val fileId = idWorker.nextId()
            val time = Date()
            fileMapper.insert(FilePO(
                    id = fileId,
                    size = dto.size,
                    mime = dto.mime,
                    uploader = dto.userID,
                    uploadTime = time
            ))
            //创建节点
            val nodeId = idWorker.nextId()
            nodeMapper.addNode(NodePO(
                    id = nodeId,
                    owner = dto.userID,
                    parent = dto.parent,
                    isFolder = false,
                    file = fileId,
                    name = dto.name,
                    status = FileConst.Status.UPLOADING.name,
                    createTime = time
            ))
            //添加缓存
            val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
            val hashKey = "${FileConst.NodeType.FILE.name}-${dto.parent}"
            hashOps[key,hashKey].fromJson<ArrayList<FileNodeBO>>()?.let {
                it.add(FileNodeBO(
                        id = nodeId,
                        name = dto.name,
                        mimeType = dto.mime,
                        status = FileConst.Status.UPLOADING.name,
                        size = dto.size,
                        createTime = time
                ))
                hashOps.put(key,hashKey,it.toJson())
            }
            return QYResult.success(data = Pair(nodeId,fileId))
        } catch (e: Exception){
        } finally {
            removeLock(lockName)
        }
        return QYResult.fail(msg = "节点创建失败")
    }

    override fun onUploadFinish(nodeID: Long, result: Boolean) {
        val targetStatus = if (result){
            FileConst.Status.NORMAL.name
        }
        else {
            FileConst.Status.FAIL.name
        }
        nodeMapper.updateFileStatus(nodeID,targetStatus)
    }
}