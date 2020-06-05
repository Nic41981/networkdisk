package com.dy.networkdisk.storage.dao

import com.dy.networkdisk.storage.po.UploadPO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UploadMapper {
    fun insert(po: UploadPO)

    fun selectChunkIDList(id: Long): List<Long>
}