package com.dy.networkdisk.web.controller.user

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.api.dto.dubbo.user.ActiveDTO
import com.dy.networkdisk.api.dto.dubbo.user.RegisterDTO
import com.dy.networkdisk.api.user.UserRegisterService
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.ConfigUtil
import com.dy.networkdisk.web.tool.IPLocation
import com.dy.networkdisk.web.tool.KaptchaUtil
import com.dy.networkdisk.web.vo.ActiveVO
import com.dy.networkdisk.web.vo.MessagePageVO
import com.dy.networkdisk.web.vo.RegisterVO
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/user")
class RegisterController @Autowired constructor(
        private val config: ConfigUtil,
        private val kaptcha: KaptchaUtil
) {
    @Reference
    private lateinit var service: UserRegisterService

    @GetMapping("/register")
    fun getRegisterPage(): String {
        return "register"
    }

    @PostMapping("/register")
    fun submitRegister(request: HttpServletRequest, model: ModelAndView, vo: RegisterVO): ModelAndView? {
        //IP信息查询(异步)
        val ip = request.getAttribute(Const.IP_KEY) as String
        val token = request.getAttribute(Const.ONLINE_TOKEN_KEY) as String
        val ipLocation = IPLocation(ip)
        //注册信息复核
        var result = vo.run {
            //邮箱检查
            if (email.trim().also { email = it }.length !in 5..50){
                return@run false
            }
            //昵称检查
            if (nickname.trim().also { nickname = it }.length > 20){
                return@run false
            }
            if (nickname.isEmpty()) {
                nickname = "游客"
            }
            //密码检查
            password = password.trim()
            if (password.trim().also { password = it }.length !in 5..20){
                return@run false
            }
            //访问域名检查
            if(host.isEmpty()){
                return@run false
            }
            return@run kaptcha.check(token, vo.verificationCode)
        }
        if (!result) {
            //TODO 管理员预警邮件
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.WANG_MSG_TYPE).apply {
                    content = arrayListOf(
                            "系统检测到您在${Calendar.YEAR}年${Calendar.MONTH}月${Calendar.DAY_OF_MONTH}日" +
                                    "${Calendar.HOUR_OF_DAY}时${Calendar.MINUTE}分提交的注册操作存在异常，" +
                                    "已经中断操作并通知管理员处理。",
                            "异常原因：非法参数"
                    )
                    jump("注册","/user/register")
                }
                model.addObject("message",message)
            }
        }
        //邀请码处理
        var inviterID = 0L
        val onlyInvite = config.getBoolean(ConfigRedisKey.WEB_REGISTER_ONLY_INVITE, false)
        inviterID = Random().nextLong() //TODO 查找邀请者ID
        if (onlyInvite && inviterID == 0L) {
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.INFO_MSG_TYPE).apply {
                    content = arrayListOf("管理员开启了注册仅邀请，请输入有效的邀请码注册。")
                    jump("注册", "/user/register")
                }
                addObject("message",message)
            }
        }
        //用户注册
        val dto = RegisterDTO().apply {
            this.email = vo.email
            this.nickname = vo.nickname
            this.password = vo.password
            this.host = vo.host
            this.ip = ipLocation.ip
            this.ipLocation = ipLocation.getLocation()
        }
        dto.inviterID = inviterID
        result = service.register(dto)
        if (!result) {
            //用户已注册
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.INFO_MSG_TYPE).apply {
                    content = arrayListOf("该邮箱已注册，您可以直接登录或者尝试找回密码。")
                    jump("登录", "/user/login")
                }
                addObject("message",message)
            }
        }
        val expire = config.getInteger(ConfigRedisKey.USER_GUESTS_EXPIRE, 12)
        return model.apply{
            viewName = "message"
            val message = MessagePageVO(Const.INFO_MSG_TYPE).apply {
                content = arrayListOf(
                        "激活邮件将发送至您的邮箱。",
                        "游客账号无法使用网盘功能，账号信息会保存${expire}个小时，请及时激活您的账号。"
                )
                jump("登录", "/user/login")
            }
            addObject("message",message)
        }
    }

    @GetMapping("/register/active")
    fun active(request: HttpServletRequest, model: ModelAndView, vo: ActiveVO): ModelAndView? {
        //IP信息查询(异步)
        val ip = request.getAttribute(Const.IP_KEY) as String
        val ipLocation = IPLocation(ip)
        //参数检查
        var result = vo.run {
            return@run email.isNotBlank() && lock.isNotBlank()
        }
        if (!result) {
            //TODO 管理员预警邮件
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.WANG_MSG_TYPE).apply {
                    content = arrayListOf(
                            "系统检测到您在${Calendar.YEAR}年${Calendar.MONTH}月${Calendar.DAY_OF_MONTH}日" +
                                    "${Calendar.HOUR_OF_DAY}时${Calendar.MINUTE}分提交的激活操作存在异常，" +
                                    "已经中断操作并通知管理员处理。",
                            "异常原因：非法参数"
                    )
                }
                addObject("message",message)
            }
        }
        val dto = ActiveDTO().apply {
            this.email = vo.email
            this.lock = vo.lock
            this.ip = ipLocation.ip
            this.ipLocation = ipLocation.getLocation()
        }
        result = service.active(dto)
        if (result) {
            return model.apply {
                viewName = "message"
                val message = MessagePageVO(Const.INFO_MSG_TYPE).apply {
                    content = arrayListOf("账号激活成功,现在您可以登录使用全部功能。")
                    jump("登录", "/user/login")
                }
                addObject("message",message)
            }
        }
        return model.apply {
            viewName = "message"
            val message = MessagePageVO(Const.WANG_MSG_TYPE).apply {
                content = arrayListOf("激活信息不存在或已失效,请重新注册。")
                jump("注册","/user/register")
            }
            addObject("message",message)
        }
    }

}