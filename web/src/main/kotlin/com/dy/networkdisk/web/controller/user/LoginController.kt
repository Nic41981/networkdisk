package com.dy.networkdisk.web.controller.user

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.*
import com.dy.networkdisk.web.vo.LoginVO
import com.dy.networkdisk.web.vo.MessagePageVO
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
@RequestMapping("/user")
class LoginController @Autowired constructor(
        private val config: ConfigUtil,
        private val kaptcha: KaptchaUtil
) {

    @Reference
    private lateinit var service: UserLoginService

    @GetMapping("/login")
    fun getPage(model: ModelAndView): ModelAndView{
        val allowRegister: Boolean = config.getBoolean(ConfigRedisKey.WEB_REGISTER_ALLOW, false)
        model.viewName = "login"
        model.addObject("allow_register", allowRegister)
        return model
    }

    @PostMapping("/login")
    fun submit(model: ModelAndView,request: HttpServletRequest,vo: LoginVO): ModelAndView{
        val ip = request.getAttribute(Const.IP_KEY) as String
        val token = request.getAttribute(Const.ONLINE_TOKEN_KEY) as String
        val ipLocation = IPLocation(ip)
        //参数检查
        val result = vo.run {
            return@run kaptcha.check(token,vo.verificationCode)
        }
        if (!result){
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.WANG_MSG_TYPE).apply {
                    content.add("系统检测到您在${Calendar.YEAR}年${Calendar.MONTH}月${Calendar.DAY_OF_MONTH}日" +
                            "${Calendar.HOUR_OF_DAY}时${Calendar.MINUTE}分提交的登录操作存在异常，已经中断操作并通知管理员处理。")
                    content.add("异常原因：非法参数")
                    jump("登录","/user/login")
                }
                addObject("message",message)
            }
        }
        if (service.isGuests(vo.email)){
            return model.apply {
                viewName = "login"
                addObject("result_title",Const.INFO_MSG_TYPE)
                addObject("result","很抱歉，您的账号未激活，游客账户无法登陆。")
                addObject("result_type","info")
            }
        }
        return model
    }
}