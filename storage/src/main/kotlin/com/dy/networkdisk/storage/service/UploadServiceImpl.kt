package com.dy.networkdisk.storage.service

import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.CreateUploadingFileDTO
import com.dy.networkdisk.api.dto.storage.BeforeUploadDTO
import com.dy.networkdisk.api.dto.storage.UploadDTO
import com.dy.networkdisk.api.file.FileUploadService
import com.dy.networkdisk.api.storage.UploadService
import com.dy.networkdisk.storage.config.Const
import com.dy.networkdisk.storage.dao.ChunkMapper
import com.dy.networkdisk.storage.dao.UploadMapper
import com.dy.networkdisk.storage.po.ChunkPO
import com.dy.networkdisk.storage.po.UploadPO
import com.dy.networkdisk.storage.tool.ConfigUtil
import com.dy.networkdisk.storage.tool.IDWorker
import com.dy.networkdisk.storage.tool.hashOps
import com.dy.networkdisk.storage.tool.template
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sun.misc.BASE64Encoder
import java.io.File
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
import org.apache.dubbo.config.annotation.Service as DubboService


@Service
@DubboService
class UploadServiceImpl @Autowired constructor(
        val idWorker: IDWorker,
        val config: ConfigUtil
): UploadService {

    @Resource
    private lateinit var chunkMapper: ChunkMapper

    @Resource
    private lateinit var uploadMapper: UploadMapper

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

    @Reference
    private lateinit var fileService: FileUploadService

    override fun beforeUpload(dto: BeforeUploadDTO): QYResult<Long> {
        val id = idWorker.nextId()
        val key = "${Const.UPLOAD_RECORD_REDIS_KEY}:${id}"
        val result = fileService.createUploadingFile(CreateUploadingFileDTO(
                sessionID = dto.sessionID,
                userID = dto.userID,
                parent = dto.parent,
                name = dto.name,
                mime = dto.mime,
                size = dto.size
        ))
        if (!result.isSuccess){
            return QYResult.fail(msg = result.msg)
        }
        //记录上传信息
        hashOps.put(key,"nodeID",result.data!!.first.toString())
        hashOps.put(key,"fileID",result.data!!.second.toString())
        hashOps.put(key,"userID",dto.userID.toString())
        hashOps.put(key,"count","0")
        hashOps.put(key,"hasError",false.toString())
        //设置超时
        val expire = config.getLong(ConfigInfo.UPLOAD_EXPIRE,defaultValue = 24)
        template.expire(key,expire,TimeUnit.HOURS)
        return QYResult.success(data = id)
    }

    override fun upload(dto: UploadDTO) {
        println(dto)
        val key = "${Const.UPLOAD_RECORD_REDIS_KEY}:${dto.id}"
        val nodeID = (hashOps[key,"nodeID"] ?: return).toLong()
        val fileID = (hashOps[key,"fileID"] ?: return).toLong()
        val userID = (hashOps[key,"userID"] ?: return).toLong()
        try {
            //读取分块
            val content = dto.file
            //校验分块信息
            val md5 = getMD5(content)
            val sha256 = getHash256(content)
            if (md5.isNotBlank() && sha256.isNotBlank()){
                //仅在md5和sha256计算正常时简化存储
                chunkMapper.findSameChunkID(
                        md5 = md5,
                        sha256 = sha256,
                        size = content.size.toLong()
                )?.let {
                    //存在相同文件块，直接更新
                    uploadMapper.insert(UploadPO(
                            id = idWorker.nextId(),
                            file = fileID,
                            chunk = it,
                            sequence = dto.chunk,
                            uploader = userID,
                            uploadTime = Date()
                    ))
                    return
                }
            }
            //加密分块
            val password = getMD5((md5 + sha256 + dto.size).toByteArray())
            val encodedContent = toAESEncode(content,password) ?: error("加密异常")
            //写入分块
            val chunkID = idWorker.nextId()
            val path = "./data/${chunkID}"
            val file = File(path)
            file.parentFile.mkdirs()
            file.createNewFile()
            file.outputStream().use {
                it.write(encodedContent)
            }
            //更新数据
            chunkMapper.insert(ChunkPO(
                    id = chunkID,
                    size = content.size.toLong(),
                    md5 = md5,
                    sha256 = sha256,
                    path = path,
                    uploader = userID,
                    uploadTime = Date()
            ))
            uploadMapper.insert(UploadPO(
                    id = idWorker.nextId(),
                    file = fileID,
                    chunk = chunkID,
                    sequence = dto.chunk,
                    uploader = userID,
                    uploadTime = Date()
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            hashOps.put(key,"hasError",true.toString())
        } finally {
            val count = hashOps.increment(key,"count",1)
            if (count.toInt() == dto.chunks){
                val hasError = (hashOps[key,"hasError"] ?: "false").toBoolean()
                fileService.onUploadFinish(nodeID,hasError)
            }
        }
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

    fun getHash256(content: ByteArray): String{
        return try {
            MessageDigest.getInstance("SHA256")
                    .digest(content)
                    .toHexString()
        } catch (e: Exception){
            ""
        }
    }

    fun toAESEncode(content: ByteArray,key: String): ByteArray?{
        kotlin.runCatching {
            val originalKey = KeyGenerator.getInstance("AES").run {
                init(128,SecureRandom.getInstance("SHA1PRNG").apply {
                    setSeed(key.toByteArray())
                })
                generateKey().encoded
            }
            val keySpec = SecretKeySpec(originalKey, "AES")
            val cipher = Cipher.getInstance("AES").apply {
                init(Cipher.ENCRYPT_MODE,keySpec)
            }
            return cipher.doFinal(content)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

}