package com.dy.networkdisk.upload.dao

import com.dy.networkdisk.upload.po.UploadPO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UploadMapper {
    fun insert(po: UploadPO)
}