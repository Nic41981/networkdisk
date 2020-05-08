package com.dy.networkdisk.api.dto.download

data class GetChunkIDListDTO(
        val nodeID: Long,
        val owner: Long,
        val isShare: Boolean
)