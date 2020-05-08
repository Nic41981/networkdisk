package com.dy.networkdisk.upload.dao

import com.dy.networkdisk.upload.po.ChunkPO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ChunkMapper {

    fun insert(po: ChunkPO)

    fun findSameChunkID(
            @Param("md5") md5: String,
            @Param("sha256") sha256: String,
            @Param("size") size: Long
    ): Long?
}