package com.dy.networkdisk.file.service

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.*
import com.dy.networkdisk.api.file.FileWebService
import com.dy.networkdisk.file.bo.FileNodeBO
import com.dy.networkdisk.file.bo.FolderNodeBO
import com.dy.networkdisk.file.config.Const
import com.dy.networkdisk.file.dao.FileMapper
import com.dy.networkdisk.file.dao.NodeMapper
import com.dy.networkdisk.file.po.NodePO
import com.dy.networkdisk.file.tool.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import org.apache.dubbo.config.annotation.Service as DubboService
import javax.annotation.Resource
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

@Service
@DubboService
class FileWebServiceImpl(
        template: StringRedisTemplate,
        private val idWorker: IDWorker
): FileWebService {

    @Resource
    private lateinit var nodeMapper: NodeMapper

    @Resource
    private lateinit var fileMapper: FileMapper

    private val hashOps = template.opsForHash<String, String>()

    /**
     * 扩展
     */
    private fun Date.format(): String {
        return SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(this)
    }

    private fun Long.format(): String {
        val suffixArray = arrayOf("B", "KB", "MB", "GB")
        var value = this.toDouble()
        for (suffix in suffixArray) {
            if (value < 1024) {
                return String.format("%.2f", value) + suffix
            }
            value /= 1024
        }
        return String.format("%.2fTB", value)
    }

    /**
     * 返回根节点ID，如果根节点不存在则创建
     * @param userID 用户ID
     * @return 根节点ID
     */
    override fun getRootID(userID: Long): Long? {
        val lockName = "${Const.LOCK_FOLDER_NAME}:${userID}:0"
        if (!getLock(name = lockName)) {
            return null
        }
        try {
            nodeMapper.findUserRootID(userID)?.let { return it }
            val id = idWorker.nextId()
            nodeMapper.addNode(NodePO(
                    id = id,
                    owner = userID,
                    parent = 0L,
                    isFolder = true,
                    name = "root",
                    status = FileConst.Status.NORMAL.name,
                    createTime = Date()
            ))
            return id
        } finally {
            removeLock(lockName)
        }
    }

    /**
     * 获取目录树子节点
     * @param sessionID 会话ID
     * @param userID 用户ID
     * @return 节点数组
     */
    override fun getChildrenFolderTree(sessionID: Long, userID: Long, parent: Long): QYResult<Map<Long, String>> {
        val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val hashKey = "${FileConst.NodeType.FOLDER.name}-${parent}"
        //读取缓存
        hashOps[key, hashKey].fromJson<List<FolderNodeBO>>()?.let {
            val resultMap = LinkedHashMap<Long,String>()
            for (bo in it){
                resultMap[bo.id] = bo.name
            }
            return QYResult.success(data = resultMap)
        }
        //查询数据库
        val result = hashMapOf<Long, String>()
        val resultList = nodeMapper.getChildrenFolderTree(userID, parent)
        for (map in resultList) {
            val id = map["id"] as? Long? ?: continue
            result[id] = map["name"] as? String? ?: "Error"
        }
        //写缓存
        hashOps.put(key, hashKey, result.toJson())
        return QYResult.success(data = result)
    }

    /**
     * 获取指定用户指定节点下子节点列表
     */
    override fun getChildren(sessionID: Long, userID: Long, parent: Long): QYResult<List<FileNodeResult>> {
        val resultList = ArrayList<FileNodeResult>()
        val folderBOList = getChildrenFolder(sessionID, userID, parent)
        for (folder in folderBOList) {
            resultList.add(FileNodeResult(
                    id = folder.id,
                    order = 0,
                    name = folder.name,
                    type = FileConst.NodeType.FOLDER.name,
                    status = FileConst.Status.NORMAL.name,
                    size = "-",
                    createTime = folder.createTime.format()
            ))
        }
        val fileBOList = getChildrenFile(sessionID, userID, parent)
        for (file in fileBOList) {
            resultList.add(FileNodeResult(
                    id = file.id,
                    order = 1,
                    name = file.name,
//                    TODO 图标适配
//                    type = file.mimeType,
                    type = FileConst.NodeType.FILE.name,
                    status = file.status,
                    size = file.size.format(),
                    createTime = file.createTime.format()
            ))
        }
        return QYResult.success(data = resultList)
    }

    private fun getChildrenFolder(sessionID: Long, userID: Long, parent: Long): List<FolderNodeBO> {
        val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val hashKey = "${FileConst.NodeType.FOLDER.name}-${parent}"
        //读缓存
        hashOps[key, hashKey].fromJson<List<FolderNodeBO>>()?.let {
            return it
        }
        val boList = arrayListOf<FolderNodeBO>()
        val poList = nodeMapper.selectChildrenFolder(
                userID = userID,
                parent = parent
        )
        for (po in poList) {
            boList.add(FolderNodeBO(
                    id = po.id,
                    name = po.name,
                    createTime = po.createTime
            ))
        }
        //写缓存
        hashOps.put(key, hashKey, boList.toJson())
        return boList
    }

    private fun getChildrenFile(sessionID: Long, userID: Long, parent: Long): List<FileNodeBO>{
        val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val hashKey = "${FileConst.NodeType.FILE.name}-${parent}"
        //读缓存
        hashOps[key, hashKey].fromJson<List<FileNodeBO>>()?.let {
            //检查状态更新
            var hasChanged = false
            for (bo in it){
                if (bo.status != FileConst.Status.NORMAL.name){
                    val lastStatus = nodeMapper.findFileLastStatusByID(bo.id)
                    if (bo.status != lastStatus){
                        bo.status = lastStatus
                        hasChanged = true
                    }
                }
            }
            //状态改变，更新缓存
            if (hasChanged){
                hashOps.put(key,hashKey,it.toJson())
            }
            return it
        }
        //获取节点信息
        val nodeList = nodeMapper.selectChildrenFile(
                userID = userID,
                parent = parent
        )
        //节点为空直接返回
        if (nodeList.isEmpty()){
            val boList = ArrayList<FileNodeBO>()
            hashOps.put(key,hashKey,boList.toJson())
            return boList
        }
        //获取文件信息
        val idList = ArrayList<Long>(nodeList.size)
        for (node in nodeList){
            idList.add(node.id)
        }
        val fileList = fileMapper.selectSizesAndMimesByIds(idList)
        val mimeMap = HashMap<Long,String>()
        val sizeMap = HashMap<Long,Long>()
        for (file in fileList){
            val id = file["id"] as? Long? ?: continue
            mimeMap[id] = file["mime"] as? String? ?: "application/octet-stream"
            sizeMap[id] = file["size"] as? Long? ?: 0L
        }
        //组装数据
        val boList = ArrayList<FileNodeBO>(nodeList.size)
        for (po in nodeList){
            boList.add(FileNodeBO(
                    id = po.id,
                    name = po.name,
                    mimeType = mimeMap[po.id] ?: "application/octet-stream",
                    status = po.status,
                    size = sizeMap[po.id] ?: -1L,
                    createTime = po.createTime
            ))
        }
        //写缓存
        hashOps.put(key,hashKey,boList.toJson())
        return boList
    }

    override fun createFolder(dto: CreateFolderDTO): QYResult<Unit> {
        //尝试加锁
        val lockName = "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        if (!getLock(lockName)){
            return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
        }
        try {
            //权限检查
            val status = nodeMapper.checkPermission(
                    id = dto.parent,
                    parent = null,
                    userID = dto.userID,
                    isFolder = true
            )
            if (FileConst.Status.NORMAL.name != status){
                return QYResult.fail(msg = "目标不存在或状态异常，请稍后再试。")
            }
            //重名文件检查
            val result = nodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    isFolder = true,
                    name = dto.name
            )
            if (result != 0){
                return QYResult.fail(msg = "该目录下存在同名文件夹。")
            }
            //创建文件夹
            val id = idWorker.nextId()
            val date = Date()
            nodeMapper.addNode(NodePO(
                    id = id,
                    owner = dto.userID,
                    isFolder = true,
                    parent = dto.parent,
                    name = dto.name,
                    status = FileConst.Status.NORMAL.name,
                    createTime = date
            ))
            //更新缓存
            val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
            val hashKey = "${FileConst.NodeType.FOLDER.name}-${dto.parent}"
            val boList = hashOps[sessionKey,hashKey].fromJson<ArrayList<FolderNodeBO>>()
            boList?.let {
                it.add(FolderNodeBO(
                        id = id,
                        name = dto.name,
                        createTime = date
                ))
                hashOps.put(sessionKey,hashKey,it.toJson())
            }
            return QYResult.success()
        } catch (e: Exception){
            return QYResult.fail(msg = "创建失败！")
        } finally {
            removeLock(lockName)
        }
    }

    override fun rename(dto: RenameDTO): QYResult<Unit> {
        //加锁
        val isFolder = dto.type.equals(FileConst.NodeType.FOLDER.name,ignoreCase = true)
        val lockName = if (isFolder){
            "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        }
        else {
            "${Const.LOCK_FILE_NAME}:${dto.userID}:${dto.parent}"
        }
        if (!getLock(name = lockName)){
            return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
        }
        try {
            //查询原信息校验
            val status = nodeMapper.checkPermission(
                    id = dto.id,
                    parent = dto.parent,
                    userID = dto.userID,
                    isFolder = isFolder
            )
            if (FileConst.Status.NORMAL.name != status){
                return QYResult.fail(msg = "目标不存在或状态异常，请稍后再试。")
            }
            //重名查询
            val result = nodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    isFolder = isFolder,
                    name = dto.newName
            )
            if (result != 0){
                return QYResult.fail(msg = "该目录下存在同名文件或文件夹。")
            }
            //重命名
            nodeMapper.rename(dto.id,dto.newName)
            //缓存更新
            val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
            if (isFolder){
                val hashKey = "${FileConst.NodeType.FOLDER.name}-${dto.parent}"
                if (hashOps.hasKey(key,hashKey)){
                    hashOps.get(key,hashKey).fromJson<ArrayList<FolderNodeBO>>()?.let {
                        for (bo in it){
                            if (bo.id == dto.id){
                                bo.name = dto.newName
                                break
                            }
                        }
                        hashOps.put(key,hashKey,it.toJson())
                    }
                }
            }
            else {
                val hashKey = "${FileConst.NodeType.FILE.name}-${dto.parent}"
                if (hashOps.hasKey(key, hashKey)){
                    hashOps.get(key, hashKey).fromJson<ArrayList<FileNodeBO>>()?.let {
                        for (bo in it){
                            if (bo.id == dto.id){
                                bo.name = dto.newName
                                break
                            }
                        }
                        hashOps.put(key,hashKey,it.toJson())
                    }
                }
            }
            return QYResult.success()
        } catch (e: Exception){
            return QYResult.fail(msg = "重命名失败！")
        } finally {
            removeLock(lockName)
        }
    }

    override fun delete(dto: DeleteDTO): QYResult<Unit> {
        //加锁
        val isFolder = dto.type.equals(FileConst.NodeType.FOLDER.name,ignoreCase = true)
        val lockName = if (isFolder){
            "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        }
        else {
            "${Const.LOCK_FILE_NAME}:${dto.userID}:${dto.parent}"
        }
        if (!getLock(lockName)){
            return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
        }
        try {
            //检查状态
            val status = nodeMapper.checkPermission(
                    id = dto.id,
                    parent = dto.parent,
                    userID = dto.userID,
                    isFolder = isFolder
            )
            if (status.isNullOrBlank() || FileConst.Status.DELETE.name == status){
                return QYResult.success()
            }
            if (!isFolder){
                try {
                    //删除文件节点
                    nodeMapper.delete(dto.id)
                    return QYResult.success()
                } finally {
                    //删除父级文件缓存
                    val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
                    val hashKey = "${FileConst.NodeType.FILE.name}-${dto.parent}"
                    hashOps.delete(key, hashKey)
                }
            }
            else {
                try {
                    //删除文件夹节点
                    if (doDelete(dto.sessionID,dto.userID,dto.id)){
                        return QYResult.success()
                    }
                    return QYResult.fail(msg = "部分文件删除失败。")
                } finally {
                    //删除父级文件夹缓存
                    val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
                    val hashKey = "${FileConst.NodeType.FOLDER.name}-${dto.parent}"
                    hashOps.delete(key,hashKey)
                }
            }
        } catch (e: Exception){
            return QYResult.fail(msg = "删除失败。")
        } finally {
            //移除锁
            removeLock(lockName)
        }
    }

    private fun doDelete(sessionID: Long,userID: Long,parent: Long): Boolean {
        val key = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        var success = true
        /****查找并删除子文件****/
        try {
            val lockName = "${Const.LOCK_FILE_NAME}:${userID}:${parent}"
            if (getLock(lockName)){
                try {
                    nodeMapper.deleteAllChildrenFile(parent)
                } finally {
                    //删除当前节点的子文件缓存
                    val hashKey = "${FileConst.NodeType.FILE.name}-${parent}"
                    hashOps.delete(key, hashKey)
                    //移除锁
                    removeLock(lockName)
                }
            }
            else {
                success = false
            }
        } catch (e: Exception){
            success = false
        }
        /****查找并删除子目录****/
        try {
            val folderList = getChildrenFolder(sessionID,userID,parent)
            for (folder in folderList){
                //子文件夹加锁
                val lockName = "${Const.LOCK_FOLDER_NAME}:${userID}:${folder.id}"
                if (getLock(lockName)) {
                    try {
                        if (!doDelete(sessionID,userID,folder.id)) {
                            success = false
                        }
                    } finally {
                        //删除当前节点子目录缓存
                        val hashKey = "${FileConst.NodeType.FOLDER.name}-${parent}"
                        hashOps.delete(key, hashKey)
                        //移除锁
                        removeLock(lockName)
                    }
                }
                else {
                    success = false
                }
            }
        } catch (e: Exception) {
            success = false
        }
        if (success) {
            //删除成功，删除当前目录
            nodeMapper.delete(parent)
        }
        return success
    }
}