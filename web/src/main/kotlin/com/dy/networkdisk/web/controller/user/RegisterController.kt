package com.dy.networkdisk.web.controller.user

import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.user.ActiveDTO
import com.dy.networkdisk.api.dto.user.RegisterDTO
import com.dy.networkdisk.api.user.UserRegisterService
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.*
import com.dy.networkdisk.web.vo.*
import com.dy.networkdisk.web.vo.user.ActiveVO
import com.dy.networkdisk.web.vo.user.RegisterVO
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

    /*****扩展*****/
    private val HttpServletRequest.ip: String
        get() {
            return getAttribute(Const.IP_KEY) as String
        }
    private val HttpServletRequest.sessionID: Long
        get() {
            return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
        }

    @GetMapping("/register")
    fun getPage(model: ModelAndView): ModelAndView {
        val allowRegister = config.getBoolean(ConfigInfo.WEB_REGISTER_ALLOW,true)
        return if (allowRegister){
            model.apply { viewName = "register" }
        }
        else{
            model.apply {
                viewName = "message"
                addObject(MessagePageVO.NAME,MessagePageVO("提示")
                        .notAllowRegister().jumpTo("登录","/usr/login")
                )
            }
        }
    }

    @PostMapping("/register")
    fun submit(request: HttpServletRequest, model: ModelAndView, vo: RegisterVO): ModelAndView {
        //IP信息查询(异步)
        val ipLocation = IPLocation(request.ip)
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
            if (password.trim().also { password = it }.length !in 5..20){
                return@run false
            }
            //访问域名检查
            if(host.isEmpty()){
                return@run false
            }
            kaptcha.check(request.sessionID, vo.verificationCode)
        }
        if (!result) {
            //TODO 管理员预警邮件
            return model.apply {
                viewName = "message"
                model.addObject(MessagePageVO.NAME,MessagePageVO("操作异常")
                        .invalidOperation("注册","参数异常").withTime()
                        .jumpTo("注册","/user/register")
                )
            }
        }
        //邀请码处理
        var inviterID = 0L
        val onlyInvite = config.getBoolean(ConfigInfo.WEB_REGISTER_ONLY_INVITE, false)
        inviterID = Random().nextLong() //TODO 查找邀请者ID
        if (onlyInvite && inviterID == 0L) {
            return model.apply {
                viewName = "register"
                addObject(ResultDialogVO.NAME,ResultDialogVO(
                        title = "注册失败",
                        type = ResultDialogVO.TYPE_INFO,
                        content = "管理员开启了仅邀请注册，请输入邀请码完成注册。"
                ))
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
                addObject(MessagePageVO.NAME,MessagePageVO("提示")
                        .hasRegister().jumpTo("登录","/user/login")
                )
            }
        }
        val expire = config.getInteger(ConfigInfo.USER_GUESTS_EXPIRE, 12)
        return model.apply{
            viewName = "message"
            addObject(MessagePageVO.NAME,MessagePageVO("账号激活")
                    .activeEmail(expire).withTime().jumpTo("登录", "/user/login")
            )
        }
    }

    @GetMapping("/register/active")
    fun active(request: HttpServletRequest, model: ModelAndView, vo: ActiveVO): ModelAndView? {
        //IP信息查询(异步)
        val ipLocation = IPLocation(request.ip)
        //参数检查
        var result = vo.run { email.isNotBlank() && lock.isNotBlank() }
        if (!result) {
            //TODO 管理员预警邮件
            return model.apply {
                viewName = "message"
                addObject(MessagePageVO.NAME,MessagePageVO("操作异常")
                        .invalidOperation("账号激活","非法参数")
                )
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
                addObject(MessagePageVO.NAME,MessagePageVO("激活成功")
                        .activeSuccess().withTime().jumpTo("登录", "/user/login")
                )
            }
        }
        return model.apply {
            viewName = "message"
            addObject(MessagePageVO.NAME,MessagePageVO("激活失败")
                    .activeFail().jumpTo("注册","/user/register")
            )
        }
    }
}