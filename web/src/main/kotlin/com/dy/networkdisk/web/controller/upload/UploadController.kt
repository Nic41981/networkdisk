package com.dy.networkdisk.web.controller.upload

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.dto.file.CreateFileDTO
import com.dy.networkdisk.api.file.FileHomeService
import com.dy.networkdisk.api.upload.UploadService
import com.dy.networkdisk.web.tool.fromJson
import com.dy.networkdisk.web.tool.sessionID
import com.dy.networkdisk.web.tool.sessionInfo
import com.dy.networkdisk.web.tool.toJson
import com.dy.networkdisk.web.vo.ResultJsonVO
import com.dy.networkdisk.web.vo.upload.FileUploadVO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
class UploadController @Autowired constructor(
        val template: StringRedisTemplate
) {

//    @Reference
//    private lateinit var service: UploadService

    @PostMapping("/file")
    @ResponseBody
    fun upload(request: HttpServletRequest,vo: FileUploadVO): String{
        val sessionID = request.sessionID
        val userID = request.sessionInfo.id
        if (vo.file.size != 1024*1024L){

        }
        GlobalScope.launch {

        }
        TODO()
    }
}