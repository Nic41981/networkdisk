package com.dy.networkdisk.web.controller

import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.KaptchaUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class VerificationController {

    @Autowired
    private lateinit var kaptcha: KaptchaUtil

    @GetMapping("/verification")
    fun getImage(request: HttpServletRequest, response: HttpServletResponse) {
        val token = request.getAttribute(Const.ONLINE_TOKEN_KEY) ?: return
        with(response) {
            setDateHeader("Expires", 0)
            setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
            addHeader("Cache-Control", "post-check=0, pre-check=0")
            setHeader("Pragma", "no-cache")
            contentType = "image/jpeg"
        }
        val code = kaptcha.createText(token as String)
        response.outputStream.use { out ->
            ImageIO.write(kaptcha.createImage(code), "jpeg", out)
            out.flush()
        }
    }

    @ResponseBody
    @PostMapping("/verification")
    fun checkAnswer(request: HttpServletRequest,code: String): String{
        val token = request.getAttribute(Const.ONLINE_TOKEN_KEY) ?: return "false"
        return if (kaptcha.check(token as String, code)) "true" else "false"
    }
}