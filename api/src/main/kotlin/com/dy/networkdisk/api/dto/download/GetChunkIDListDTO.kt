package com.dy.networkdisk.api.dto.download

import com.dy.networkdisk.api.annotation.NoArg
import java.util.*

@NoArg
data class GetChunkIDListDTO(
        val nodeID: Long,
        val owner: Long,
        val shareTime: Date? = null
)