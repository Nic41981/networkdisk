package com.dy.networkdisk.web.controller.upload

import com.dy.networkdisk.api.dto.upload.BeforeUploadDTO
import com.dy.networkdisk.api.dto.upload.UploadDTO
import com.dy.networkdisk.api.upload.UploadService
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import sun.security.provider.MD5
import java.security.MessageDigest
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
        val result = service.beforeUpload(BeforeUploadDTO(
                sessionID = request.sessionID,
                userID = request.sessionInfo.id,
                parent = vo.parent,
                name = vo.name,
                mime = vo.mime,
                size = vo.size
        ))
        return ResultJsonVO<Long>(status = result.isSuccess,msg = result.msg).toJson()
    }

    @PostMapping("/chunk")
    fun upload(vo: UploadVO) {
        GlobalScope.launch {
            service.upload(UploadDTO(
                    id = vo.QYUploadID,
                    chunks = vo.chunks,
                    chunk = vo.chunk,
                    size = vo.size,
                    file = vo.file.inputStream
            ))
        }
    }
}