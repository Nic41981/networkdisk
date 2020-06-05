package com.dy.networkdisk.file.dao

import com.dy.networkdisk.file.po.FilePO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FileMapper {

    fun insert(po: FilePO)

    fun selectSizesAndMimesByIds(ids: List<Long>): List<Map<String,Any>>

    fun findFileByID(id: Long): FilePO
}