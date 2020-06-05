package com.dy.networkdisk.api.dto.storage

import com.dy.networkdisk.api.annotation.NoArg
import java.io.Serializable

@NoArg
data class DownloadInfoResult (
        val name: String,
        val size: Long,
        val mime: String,
        val list: List<Long>
): Serializable