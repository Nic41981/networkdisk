package com.dy.networkdisk.api.dto.storage

import com.dy.networkdisk.api.annotation.NoArg
import java.io.InputStream
import java.io.Serializable

@NoArg
data class UploadDTO(
        val id: Long,
        val chunks: Int,
        val chunk: Int,
        val size: Long,
        val file: ByteArray
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadDTO

        if (id != other.id) return false
        if (chunks != other.chunks) return false
        if (chunk != other.chunk) return false
        if (size != other.size) return false
        if (!file.contentEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + chunks
        result = 31 * result + chunk
        result = 31 * result + size.hashCode()
        result = 31 * result + file.contentHashCode()
        return result
    }
}