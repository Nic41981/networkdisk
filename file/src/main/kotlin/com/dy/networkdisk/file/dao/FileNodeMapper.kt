package com.dy.networkdisk.file.dao

import com.dy.networkdisk.file.po.FileNodePO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface FileNodeMapper {

    /**
     * 查找用户根目录ID
     * @param userID 用户ID
     * @return 当未找到时返回null
     */
    fun findUserRootID(userID: Long): Long?

    /**
     * 增加节点
     */
    fun addNode(po: FileNodePO)

    /**
     * 根据节点类型查找未删除的子节点ID
     */
    fun selectChildrenIDByNodeType(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long,
            @Param("nodeType")nodeType: String
    ): List<Long>

    /**
     * 根据节点类型查找未删除的子节点
     */
    fun selectChildrenByNodeType(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long,
            @Param("nodeType")nodeType: String
    ): List<FileNodePO>

    /**
     * 同名查询
     */
    fun checkSameNameExist(
            @Param("userID")userID: Long,
            @Param("parent")parent: Long,
            @Param("nodeType")nodeType: String,
            @Param("name")name: String
    ): Int

    /**
     * 检查操作权限(存在，状态，所属用户，类型)
     */
    fun checkPermission(
            @Param("id")id: Long,
            @Param("parent")parent: Long?,
            @Param("userID")userID: Long,
            @Param("nodeType")nodeType: String
    ): String?

    fun findFileNodeByID(id: Long): FileNodePO?

    /**
     * 重命名节点
     */
    fun rename(
            @Param("id")id: Long,
            @Param("name")name: String
    )

    /**
     * 删除节点
     */
    fun delete(id: Long)

    /**
     * 批量删除节点
     */
    fun deleteAll(idList: List<Long>)
}