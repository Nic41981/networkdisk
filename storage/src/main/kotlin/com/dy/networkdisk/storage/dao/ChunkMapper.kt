package com.dy.networkdisk.storage.dao

import com.dy.networkdisk.storage.po.ChunkPO
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

    fun findChunkByID(id: Long): ChunkPO
}