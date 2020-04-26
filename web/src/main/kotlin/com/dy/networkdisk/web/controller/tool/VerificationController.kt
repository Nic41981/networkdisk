package com.dy.networkdisk.web.controller.tool

import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.KaptchaUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/tool")
class VerificationController {

    @Autowired
    private lateinit var kaptcha: KaptchaUtil

    /*****扩展*****/
    private val HttpServletRequest.sessionID: Long
        get() {
            return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
        }

    @GetMapping("/verification")
    fun getImage(request: HttpServletRequest, response: HttpServletResponse) {
        with(response) {
            setDateHeader("Expires", 0)
            setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
            addHeader("Cache-Control", "post-check=0, pre-check=0")
            setHeader("Pragma", "no-cache")
            contentType = "image/jpeg"
        }
        val code = kaptcha.createText(request.sessionID)
        response.outputStream.use { out ->
            ImageIO.write(kaptcha.createImage(code), "jpeg", out)
            out.flush()
        }
    }

    @ResponseBody
    @PostMapping("/verification")
    fun checkAnswer(request: HttpServletRequest,code: String): String{
        return if (kaptcha.check(request.sessionID, code)) "true" else "false"
    }
}