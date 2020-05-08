package com.dy.networkdisk.file.dao

import com.dy.networkdisk.file.po.NodePO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface NodeMapper {

    /**
     * 查找用户根目录ID
     * @param userID 用户ID
     * @return 当未找到时返回null
     */
    fun findUserRootID(userID: Long): Long?

    /**
     * 增加节点
     */
    fun addNode(po: NodePO)

    fun getChildrenFolderTree(
            @Param("userID") userID: Long,
            @Param("parent") parent: Long
    ): List<Map<String,String>>

    /**
     * 查询文件最新状态
     */
    fun findFileLastStatusByID(id: Long): String

    /**
     * 查找目录子节点
     */
    fun selectChildrenFolder(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long
    ): List<NodePO>

    /**
     * 查找文件子节点
     */
    fun selectChildrenFile(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long
    ): List<NodePO>

    /**
     * 同名查询
     */
    fun checkSameNameExist(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long,
            @Param("isFolder")isFolder: Boolean,
            @Param("name")name: String
    ): Int

    /**
     * 检查操作权限(存在，状态，所属用户，类型)
     */
    fun checkPermission(
            @Param("id")id: Long,
            @Param("parent")parent: Long?,
            @Param("userID")userID: Long,
            @Param("isFolder")isFolder: Boolean
    ): String?

    /**
     * 重命名节点
     */
    fun rename(
            @Param("id")id: Long,
            @Param("name")name: String
    )

    /**
     * 更新文件节点上传状态
     */
    fun updateFileStatus(
            @Param("id")id: Long,
            @Param("status")status: String
    )

    /**
     * 删除节点
     */
    fun delete(id: Long)

    /**
     * 删除所有子文件节点
     */
    fun deleteAllChildrenFile(parent: Long)
}