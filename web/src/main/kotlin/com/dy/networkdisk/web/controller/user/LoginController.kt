package com.dy.networkdisk.web.controller.user

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.api.dto.dubbo.user.LoginDTO
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.*
import com.dy.networkdisk.web.vo.LoginVO
import com.dy.networkdisk.web.vo.MessagePageVO
import com.dy.networkdisk.web.vo.ResultPageVO
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
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
        val result = kaptcha.check(token,vo.verificationCode)
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
        val userType = service.getUserType(vo.email)
        //账户不存在
        if ("unknown" == userType){
            return model.apply {
                viewName = "login"
                addObject("result",ResultPageVO().apply {
                    content = "用户名或密码错误！"
                    type = "warning"
                })
            }
        }
        //游客账户
        if ("guests" == userType){
            return model.apply {
                viewName = "login"
                addObject("result",ResultPageVO().apply {
                    content = "抱歉，您的账号未激活，游客账户无法登陆。"
                    type = "info"
                })
            }
        }
        //服务器限制登录
        val loginLimit = config.getBoolean(ConfigRedisKey.WEB_LOGIN_LIMIT,false)
        if (loginLimit && "normal" == userType) {
            return model.apply {
                viewName = "login"
                addObject("result", ResultPageVO().apply {
                    content = "服务器维护中，请稍后再试。"
                    type = "info"
                })
            }
        }
        val dto = LoginDTO().apply {
            this.token = token
            this.email = vo.email
            this.password = vo.password
            this.ip = ipLocation.ip
            this.ipLocation = ipLocation.getLocation()
        }
        val loginResult = service.login(dto)
        if (loginResult.isSuccess){
            //TODO 跳转主页
        }
        return model.apply {
            viewName = "login"
            addObject("result", ResultPageVO().apply {
                content = loginResult.content
                type = loginResult.type
            })
        }
    }
}