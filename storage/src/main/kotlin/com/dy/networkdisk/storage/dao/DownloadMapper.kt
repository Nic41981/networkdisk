package com.dy.networkdisk.storage.dao

import com.dy.networkdisk.storage.po.DownloadPO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface DownloadMapper {

    fun insert(po: DownloadPO)

}