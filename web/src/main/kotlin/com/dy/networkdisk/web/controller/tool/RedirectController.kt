package com.dy.networkdisk.web.controller.tool

import com.dy.networkdisk.web.config.Const
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class RedirectController {

    @GetMapping("/tool/redirect")
    fun redirect(request: HttpServletRequest, response: HttpServletResponse) {
        val target = request.getAttribute(Const.REDIRECT_TARGET) ?: return
        try {
            response.sendRedirect(target as String)
        } catch (e: Exception) {

        }
    }
}