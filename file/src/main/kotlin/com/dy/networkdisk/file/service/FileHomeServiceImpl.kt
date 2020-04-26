package com.dy.networkdisk.file.service

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.*
import com.dy.networkdisk.api.file.FileHomeService
import com.dy.networkdisk.file.bo.FileNodeBO
import com.dy.networkdisk.file.bo.FolderNodeBO
import com.dy.networkdisk.file.config.Const
import com.dy.networkdisk.file.dao.FileNodeMapper
import com.dy.networkdisk.file.po.FileNodePO
import com.dy.networkdisk.file.tool.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import org.apache.dubbo.config.annotation.Service as DubboService
import javax.annotation.Resource
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
@DubboService
class FileHomeServiceImpl(
        private val template: StringRedisTemplate,
        private val idWorker: IDWorker
): FileHomeService {

    @Resource
    private lateinit var fileNodeMapper: FileNodeMapper

    private val hashOps = template.opsForHash<String,String>()

    /**
     * 扩展
     */
    private fun Date.format(): String {
        return SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(this)
    }
    private fun Long.format(): String {
        val suffixArray = arrayOf("B","KB","MB","GB")
        var value = this.toDouble()
        for (suffix in suffixArray){
            if (value < 1024){
                return String.format("%.2f",value) + suffix
            }
            value /= 1024
        }
        return String.format("%.2fTB",value)
    }

    /**
     * 返回根节点ID，如果根节点不存在则创建
     * @param userID 用户ID
     * @return 根节点ID
     */
    override fun getRootID(userID: Long): Long? {
        val lockName = "${Const.LOCK_FOLDER_NAME}:${userID}:0"
        if (!getLock(name = lockName)){
            return null
        }
        try {
            fileNodeMapper.findUserRootID(userID)?.let { return it }
            val id = idWorker.nextId()
            fileNodeMapper.addNode(FileNodePO(
                    id = id,
                    userID = userID,
                    nodeType = FileConst.NodeType.FOLDER.name,
                    mimeType = FileConst.MimeType.ROOT.name,
                    parent = 0L,
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
     * 获取指定节点的目录子节点信息
     */
    private fun getFolderNodeBOList(sessionID: Long, userID: Long, nodeID: Long): List<FolderNodeBO>{
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val hashKey = "${FileConst.NodeType.FOLDER.name}-${nodeID}"
        val value = hashOps[sessionKey,hashKey]
        var boList = value.fromJson<List<FolderNodeBO>>()
        if (boList == null){
            val poList = fileNodeMapper.selectChildrenByNodeType(
                    userID = userID,
                    parent = nodeID,
                    nodeType = FileConst.NodeType.FOLDER.name
            )
            boList = ArrayList(poList.size)
            for (po in poList){
                boList.add(FolderNodeBO(
                        id = po.id,
                        name = po.name,
                        mineType = po.mimeType,
                        createTime = po.createTime
                ))
            }
            hashOps.put(sessionKey,hashKey,boList.toJson())
        }
        return boList
    }

    private fun getFileNodeBOList(sessionID: Long, userID: Long, nodeID: Long): List<FileNodeBO> {
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val hashKey = "${FileConst.NodeType.FILE.name}-${nodeID}"
        val value = hashOps[sessionKey,hashKey]
        var boList = value.fromJson<List<FileNodeBO>>()
        if (boList == null){
            val poList = fileNodeMapper.selectChildrenByNodeType(
                    userID = userID,
                    parent = nodeID,
                    nodeType = FileConst.NodeType.FILE.name
            )
            boList = ArrayList(poList.size)
            for (po in poList){
                boList.add(FileNodeBO(
                        id = po.id,
                        name = po.name,
                        mimeType = po.mimeType ?: "Unknown",
                        status = po.status,
                        size = po.size ?: 0L,
                        createTime = po.createTime
                ))
            }
            hashOps.put(sessionKey,hashKey,boList.toJson())
        }
        return boList
    }

    /**
     * 获取目录树子节点
     * @param sessionID 会话ID
     * @param userID 用户ID
     * @return 节点数组
     */
    override fun getFolderTreeChildren(sessionID: Long, userID: Long, nodeID: Long): QYResult<Map<Long, String>> {
        val boList = getFolderNodeBOList(sessionID, userID, nodeID)
        val childrenMap = HashMap<Long,String>()
        for (bo in boList){
            childrenMap[bo.id] = bo.name
        }
        return QYResult.success(data = childrenMap)
    }

    /**
     * 获取指定用户指定节点下子节点列表
     */
    override fun getChildren(sessionID: Long, userID: Long, nodeID: Long): QYResult<List<FileNodeResult>> {
        val resultList = ArrayList<FileNodeResult>()
        val folderBOList = getFolderNodeBOList(sessionID,userID,nodeID)
        for (folder in folderBOList){
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
        val fileBOList = getFileNodeBOList(sessionID,userID,nodeID)
        for (file in fileBOList){
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

    override fun createFolder(dto: CreateFolderDTO): QYResult<Unit> {
        //尝试加锁
        val lockName = "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        if (!getLock(lockName)){
            return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
        }
        try {
            //权限检查
            val status = fileNodeMapper.checkPermission(
                    id = dto.parent,
                    parent = null,
                    userID = dto.userID,
                    nodeType = FileConst.NodeType.FOLDER.name
            )
            if (FileConst.Status.NORMAL.name != status){
                return QYResult.fail(msg = "目标不存在或状态异常，请稍后再试。")
            }
            //重名文件检查
            val result = fileNodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    nodeType = FileConst.NodeType.FOLDER.name,
                    name = dto.name
            )
            if (result != 0){
                return QYResult.fail(msg = "该目录下存在同名文件夹。")
            }
            //创建文件夹
            val id = idWorker.nextId()
            val date = Date()
            fileNodeMapper.addNode(FileNodePO(
                    id = id,
                    userID = dto.userID,
                    nodeType = FileConst.NodeType.FOLDER.name,
                    parent = dto.parent,
                    name = dto.name,
                    status = FileConst.Status.NORMAL.name,
                    createTime = date
            ))
            //更新缓存
            val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
            val hashKey = "${FileConst.NodeType.FOLDER.name.toLowerCase()}-${dto.parent}"
            val ops = template.opsForHash<String,String>()
            val boList = ops[sessionKey,hashKey].fromJson<ArrayList<FolderNodeBO>>()
            boList?.let {
                it.add(FolderNodeBO(
                        id = id,
                        name = dto.name,
                        createTime = date
                ))
                ops.put(sessionKey,hashKey,it.toJson())
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
        val nodeType: String
        val lockName: String
        if (dto.type == FileConst.NodeType.FOLDER.name){
            nodeType = FileConst.NodeType.FOLDER.name
            lockName = "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        }
        else {
            nodeType = FileConst.NodeType.FILE.name
            lockName = "${Const.LOCK_FILE_NAME}:${dto.userID}:${dto.parent}"
        }
        try {
            if (!getLock(name = lockName)){
                return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
            }
            //查询原信息校验
            val status = fileNodeMapper.checkPermission(
                    id = dto.id,
                    parent = dto.parent,
                    userID = dto.userID,
                    nodeType = nodeType
            )
            if (FileConst.Status.NORMAL.name != status){
                return QYResult.fail(msg = "目标不存在或状态异常，请稍后再试。")
            }
            //重名查询
            val result = fileNodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    nodeType = nodeType,
                    name = dto.newName
            )
            if (result != 0){
                return QYResult.fail(msg = "该目录下存在同名文件或文件夹。")
            }
            //重命名
            fileNodeMapper.rename(dto.id,dto.newName)
            //缓存失效(与其反序列化遍历缓存，不如直接删除缓存)
            val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
            val hashKey = "${nodeType.toLowerCase()}-${dto.parent}"
            hashOps.delete(sessionKey,hashKey)
            return QYResult.success()
        } catch (e: Exception){
            return QYResult.fail(msg = "重命名失败！")
        } finally {
            removeLock(lockName)
        }
    }

    override fun delete(dto: DeleteDTO): QYResult<Unit> {
        //加锁
        val nodeType: String
        val lockName: String
        if (dto.type == FileConst.NodeType.FOLDER.name){
            nodeType = FileConst.NodeType.FOLDER.name
            lockName = "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        }
        else {
            nodeType = FileConst.NodeType.FILE.name
            lockName = "${Const.LOCK_FILE_NAME}:${dto.userID}:${dto.parent}"
        }
        if (!getLock(lockName)){
            return QYResult.fail(msg = "您的操作太频繁,请稍后再试。")
        }
        try {
            val status = fileNodeMapper.checkPermission(
                    id = dto.id,
                    parent = dto.parent,
                    userID = dto.userID,
                    nodeType = nodeType
            )
            if (status.isNullOrBlank() || FileConst.Status.DELETE.name == status){
                return QYResult.success()
            }
            if (nodeType == FileConst.NodeType.FILE.name){
                try {
                    //删除文件节点
                    fileNodeMapper.delete(dto.id)
                    return QYResult.success()
                } finally {
                    //删除父级文件缓存
                    val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
                    val hashKey = "${FileConst.NodeType.FILE.name.toLowerCase()}-${dto.parent}"
                    hashOps.delete(sessionKey, hashKey)
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
                    val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${dto.sessionID}"
                    val hashKey = "${FileConst.NodeType.FOLDER.name.toLowerCase()}-${dto.parent}"
                    hashOps.delete(sessionKey,hashKey)
                }
            }
        } catch (e: Exception){
            return QYResult.fail(msg = "删除失败。")
        } finally {
            //移除锁
            removeLock(lockName)
        }
    }

    override fun createFile(dto: CreateFileDTO): QYResult<Unit> {
        val lockName = "${Const.LOCK_FOLDER_NAME}:${dto.userID}:${dto.parent}"
        if (!getLock(name = lockName)){
            return QYResult.fail("")
        }
        try {
            val status = fileNodeMapper.checkPermission(
                    id = dto.parent,
                    parent = null,
                    userID = dto.userID,
                    nodeType = FileConst.NodeType.FOLDER.name
            )
            if (status != FileConst.Status.NORMAL.name){
                return QYResult.fail("")
            }
            val result = fileNodeMapper.checkSameNameExist(
                    userID = dto.userID,
                    parent = dto.parent,
                    nodeType = FileConst.NodeType.FILE.name,
                    name = dto.name
            )
            if (result != 0){
                return QYResult.fail("")
            }
            fileNodeMapper.addNode(FileNodePO(
                    id = idWorker.nextId(),
                    userID = dto.userID,
                    parent = dto.parent,
                    nodeType = FileConst.NodeType.FILE.name,
                    mimeType = null,
                    name = dto.name,
                    status = FileConst.Status.UPLOADING.name,
                    size = null,
                    createTime = Date()
            ))
            return QYResult.fail("")
        } catch (e: Exception) {
            return QYResult.fail("")
        } finally {
            removeLock(lockName)
        }
    }

    private fun doDelete(sessionID: Long,userID: Long,parent: Long): Boolean {
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        var hashKey: String
        var success = true
        /****查找并删除子文件****/
        try {
            //查找子文件ID
            val idList = fileNodeMapper.selectChildrenIDByNodeType(
                    userID = userID,
                    parent = parent,
                    nodeType = FileConst.NodeType.FILE.name
            ).toMutableList()
            //子文件加锁
            for (id in idList) {
                val lockName = "${Const.LOCK_FILE_NAME}:${userID}:${id}"
                if (!getLock(lockName)) {
                    success = false
                    idList.remove(element = id)
                }
            }
            //批量删除子文件
            try {
                if (idList.isNotEmpty()) {
                    fileNodeMapper.deleteAll(idList)
                }
            } catch (e: Exception) {
                success = false
            } finally {
                //移除锁
                for (id in idList) {
                    val lockName = "${Const.LOCK_FILE_NAME}:${userID}:${id}"
                    removeLock(lockName)
                }
            }
        } finally {
            //删除当前节点的子文件缓存
            hashKey = "${FileConst.NodeType.FILE.name}-${parent}"
            hashOps.delete(sessionKey, hashKey)
        }
        /****查找并删除子目录****/
        try {
            //查找子文件夹
            val idList = fileNodeMapper.selectChildrenIDByNodeType(
                    userID = userID,
                    parent = parent,
                    nodeType = FileConst.NodeType.FOLDER.name
            )
            for (id in idList) {
                //子文件夹加锁
                val lockName = "${Const.LOCK_FOLDER_NAME}:${userID}:${id}"
                if (!getLock(lockName)) {
                    success = false
                    continue
                }
                //递归删除子目录
                try {
                    if (!doDelete(sessionID,userID,id)) {
                        success = false
                    }
                } catch (e: Exception) {
                    success = false
                } finally {
                    removeLock(lockName)
                }
            }
        } catch (e: Exception) {
            success = false
        } finally {
            //删除当前节点子目录缓存
            hashKey = "${FileConst.NodeType.FOLDER.name.toLowerCase()}:${parent}"
            hashOps.delete(sessionKey, hashKey)
        }
        if (success) {
            //删除成功，删除当前目录
            fileNodeMapper.delete(parent)
        }
        return success
    }
}