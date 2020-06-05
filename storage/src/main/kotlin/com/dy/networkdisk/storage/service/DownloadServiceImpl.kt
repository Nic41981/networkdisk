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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sun.misc.BASE64Decoder
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.annotation.Resource
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
import org.apache.dubbo.config.annotation.Service as DubboService


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
        println(file)
        val list = uploadMapper.selectChunkIDList(file!!.id)
        //记录下载信息
        downloadMapper.insert(DownloadPO(
                id = idWorker.nextId(),
                node = dto.nodeID,
                isShare = false,
                downloadTime = Date()
        ))
        return QYResult.success(data = DownloadInfoResult(
                name = file.name,
                size = file.size,
                mime = file.mime,
                list = list
        ))
    }

    override fun getChunk(id: Long): QYResult<ByteArray> {
        val chunk = chunkMapper.findChunkByID(id)
        val password = getMD5((chunk.md5 + chunk.sha256 + chunk.size).toByteArray())
        val file = File(chunk.path)
        val encodedContent = file.inputStream().use { it.readBytes() }
        val decodeContent = toAESDecode(encodedContent,password) ?: return QYResult.fail(msg = "解密失败")
        return QYResult.success(data = decodeContent)
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

    fun toAESDecode(content: ByteArray,key: String): ByteArray?{
        kotlin.runCatching {
            val originalKey = KeyGenerator.getInstance("AES").run {
                init(128, SecureRandom.getInstance("SHA1PRNG").apply {
                    setSeed(key.toByteArray())
                })
                generateKey().encoded
            }
            val keySpec = SecretKeySpec(originalKey,"AES")
            val cipher = Cipher.getInstance("AES").apply {
                init(Cipher.DECRYPT_MODE,keySpec)
            }
            return cipher.doFinal(content)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }
}