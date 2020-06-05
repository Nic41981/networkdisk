package com.dy.networkdisk.web.controller.upload

import com.dy.networkdisk.api.dto.storage.BeforeUploadDTO
import com.dy.networkdisk.api.dto.storage.UploadDTO
import com.dy.networkdisk.api.storage.UploadService
import com.dy.networkdisk.web.tool.sessionID
import com.dy.networkdisk.web.tool.sessionInfo
import com.dy.networkdisk.web.tool.toJson
import com.dy.networkdisk.web.vo.ResultJsonVO
import com.dy.networkdisk.web.vo.upload.BeforeUploadVO
import com.dy.networkdisk.web.vo.upload.UploadVO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/upload")
class UploadController @Autowired constructor(
        val template: StringRedisTemplate
) {

    @Reference
    private lateinit var service: UploadService

    @GetMapping("/before")
    @ResponseBody
    fun beforeUpload(request: HttpServletRequest,vo: BeforeUploadVO): String{
        val mParent = vo.parent.toLongOrNull(16) ?: return ResultJsonVO<Long>(status = false,msg = "参数错误！").toJson()
        val result = service.beforeUpload(BeforeUploadDTO(
                sessionID = request.sessionID,
                userID = request.sessionInfo.id,
                parent = mParent,
                name = vo.name,
                mime = vo.mime,
                size = vo.size
        ))
        return ResultJsonVO(status = result.isSuccess,msg = result.msg,data = result.data).toJson()
    }

    @PostMapping("/chunk")
    @ResponseBody
    fun upload(
            @RequestParam("task")task: Long,
            @RequestParam(value = "chunks")chunks: Int?,
            @RequestParam(value = "chunk")chunk: Int?,
            @RequestParam("size")size: Long,
            @RequestParam("file")file: MultipartFile
    ) {
        val content = file.inputStream.use {
            it.readBytes()
        }
        GlobalScope.launch {
            service.upload(UploadDTO(
                    id = task,
                    chunks = chunks ?: 1,
                    chunk = chunk ?: 1,
                    size = size,
                    file = content
            ))
            println("task=${task};chunk=${chunk}/${chunks};content=${content}")
        }
    }
}