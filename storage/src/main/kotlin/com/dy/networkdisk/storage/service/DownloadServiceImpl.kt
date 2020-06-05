package com.dy.networkdisk.storage.service

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.download.GetChunkIDListDTO
import com.dy.networkdisk.api.dto.storage.DownloadInfoResult
import com.dy.networkdisk.api.file.FileDownloadService
import com.dy.networkdisk.api.storage.DownloadService
import com.dy.networkdisk.storage.dao.ChunkMapper
import com.dy.networkdisk.storage.dao.DownloadMapper
import com.dy.networkdisk.storage.dao.UploadMapper
import com.dy.networkdisk.storage.po.DownloadPO
import com.dy.networkdisk.storage.tool.IDWorker
import org.apache.dubbo.config.annotation.Reference
import org.apache.dubbo.config.annotation.Service as DubboService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.security.MessageDigest
import java.util.*
import javax.annotation.Resource
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
@DubboService
class DownloadServiceImpl @Autowired constructor(
        val idWorker: IDWorker
) : DownloadService {

    @Reference
    private lateinit var fileService: FileDownloadService

    @Resource
    private lateinit var uploadMapper: UploadMapper

    @Resource
    private lateinit var downloadMapper: DownloadMapper

    @Resource
    private lateinit var chunkMapper: ChunkMapper

    private fun ByteArray.toHexString(): String{
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        val result = CharArray(this.size * 2)
        var p = 0
        for (it in this){
            result[p++] = hexDigits[it.toInt().ushr(4).and(0xf)]
            result[p++] = hexDigits[it.toInt().and(0xf)]
        }
        return String(result)
    }

    override fun getChunkIDList(dto: GetChunkIDListDTO): QYResult<DownloadInfoResult> {
        //查询文件信息
        val result = fileService.checkFileInfo(dto.nodeID,dto.owner)
        if (!result.isSuccess){
            return QYResult.fail(msg = result.msg)
        }
        val file = result.data
        //查询分块列表
        val list = uploadMapper.selectChunkIDList(file!!.id)
        //记录下载信息
        downloadMapper.insert(DownloadPO(
                id = idWorker.nextId(),
                node = dto.nodeID,
                isShare = false,
                downloadTime = Date()
        ))
        return QYResult.success(data = DownloadInfoResult(
                size = file.size,
                mime = file.mime,
                list = list
        ))
    }

    override fun getChunk(id: Long): QYResult<ByteArray> {
        val chunk = chunkMapper.findChunkByID(id)
        val password = getMD5((chunk.md5 + chunk.sha256 + chunk.size).toByteArray()).toByteArray()
        val file = File(chunk.path)
        val encodedContent = file.inputStream().use { it.readBytes() }
        return QYResult.success(data = getAESDecode(encodedContent,password))
    }

    fun getMD5(content: ByteArray): String{
        return try {
            MessageDigest.getInstance("MD5")
                    .digest(content)
                    .toHexString()
        } catch (e: Exception){
            ""
        }
    }

    fun getAESDecode(content: ByteArray,key: ByteArray): ByteArray{
        val sKey = SecretKeySpec(key,"AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE,sKey)
        return cipher.doFinal(content)
    }
}