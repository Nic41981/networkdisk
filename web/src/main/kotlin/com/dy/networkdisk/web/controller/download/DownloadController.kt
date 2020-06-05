package com.dy.networkdisk.web.controller.download

import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.download.GetChunkIDListDTO
import com.dy.networkdisk.api.storage.DownloadService
import com.dy.networkdisk.web.tool.ConfigUtil
import com.dy.networkdisk.web.tool.sessionInfo
import kotlinx.coroutines.*
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/download")
class DownloadController @Autowired constructor(
        val config: ConfigUtil
) {

    @Reference
    private lateinit var service: DownloadService

    @RequestMapping
    @ResponseBody
    fun download(request: HttpServletRequest,response: HttpServletResponse,id: String){
        val mid = id.toLongOrNull(16) ?: return
        val infoResult = service.getChunkIDList(dto = GetChunkIDListDTO(
                nodeID = mid,
                owner = request.sessionInfo.id
        ))
        if (infoResult.isSuccess){
            val info = infoResult.data!!
            //设置相应信息
            response.contentType = info.mime
            response.setContentLengthLong(info.size)
            //创建缓存池
            val cacheSize = config.getInteger(ConfigInfo.DOWNLOAD_CACHE_SIZE,2) + 1
            val cache = Array<Deferred<ByteArray?>?>(cacheSize) {null}
            val iterator = info.list.iterator()
            //初始化缓存
            for (i in 0 until cacheSize){
                if (iterator.hasNext()){
                    cache[i] = cacheChunkAsync(iterator.next())
                }
                else {
                    cache[i] = null
                }
            }
            var p = 0
            response.outputStream.use {
                while (true) {
                    //任务为空表示结束
                    if (cache[p] == null) {
                        break
                    }
                    //等待读取缓冲
                    val data = runBlocking {
                        cache[p]!!.await()
                    }
                    //缓冲为空表示异常，否则写入响应流
                    if (data == null) {
                        response.sendError(-1, "下载失败！")
                    } else {
                        it.write(data)
                        it.flush()
                    }
                    //缓存任务更新
                    if (iterator.hasNext()){
                        cache[p] = cacheChunkAsync(iterator.next())
                    }
                    else {
                        cache[p] = null
                    }
                    //缓存指针移动
                    if (++p == cacheSize) {
                        p = 0
                    }
                }
            }
        }
    }

    private fun cacheChunkAsync(id: Long): Deferred<ByteArray?> {
        return GlobalScope.async(Dispatchers.IO) {
            val result = service.getChunk(id)
            if (result.isSuccess){
                return@async result.data
            }
            else {
                return@async null
            }
        }
    }
}