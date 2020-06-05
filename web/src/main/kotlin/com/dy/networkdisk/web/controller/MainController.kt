package com.dy.networkdisk.web.controller

import com.dy.networkdisk.api.file.FileWebService
import com.dy.networkdisk.web.tool.sessionInfo
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@Controller
class MainController @Autowired constructor(

) {

    @Reference
    private lateinit var service: FileWebService

    @GetMapping("/")
    fun getPage(model: ModelAndView,request: HttpServletRequest): ModelAndView{
        val rootID = service.getRootID(request.sessionInfo.id) ?: error("")
        return model.apply {
            viewName = "home"
            addObject("nickname",request.sessionInfo.nickname)
            addObject("rootID", rootID.toString(16))
        }
    }
}