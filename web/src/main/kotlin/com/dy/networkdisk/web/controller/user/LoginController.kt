package com.dy.networkdisk.web.controller.user

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.user.LoginDTO
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.web.bean.QYSessionInfo
import com.dy.networkdisk.web.config.Const
import com.dy.networkdisk.web.tool.*
import com.dy.networkdisk.web.vo.user.LoginVO
import com.dy.networkdisk.web.vo.ResultDialogVO
import com.dy.networkdisk.web.vo.MessagePageVO
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/user")
class LoginController @Autowired constructor(
        private val config: ConfigUtil,
        private val kaptcha: KaptchaUtil,
        private val template: StringRedisTemplate,
        private val idWorker: IDWorker
) {

    @Reference
    private lateinit var service: UserLoginService

    /*****扩展*****/
    private val HttpServletRequest.ip: String
        get() {
            return getAttribute(Const.IP_KEY) as String
        }
    private val HttpServletRequest.sessionID: Long
        get() {
            return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
        }

    @GetMapping("/login")
    fun getPage(model: ModelAndView): ModelAndView{
        val allowRegister = config.getBoolean(ConfigInfo.WEB_REGISTER_ALLOW, true)
        model.viewName = "login"
        model.addObject("allow_register", allowRegister)
        return model
    }

    @PostMapping("/login")
    fun submit(model: ModelAndView,request: HttpServletRequest,response: HttpServletResponse,vo: LoginVO): ModelAndView?{
        val ipLocation = IPLocation(request.ip)
        //参数检查
        val result = kaptcha.check(request.sessionID,vo.verificationCode)
        if (!result){
            return model.apply {
                viewName = "message"
                addObject(MessagePageVO.NAME, MessagePageVO("操作异常")
                        .invalidOperation("登录","参数异常").withTime()
                        .jumpTo("登录","/user/login")
                )
            }
        }
        //临时用户判断
        if (service.isGuest(vo.email,vo.password)){
            return model.apply {
                viewName = "login"
                addObject(ResultDialogVO.NAME,ResultDialogVO(
                        title = "登录失败",
                        type = ResultDialogVO.TYPE_INFO,
                        content = "抱歉，您的账号未激活，游客账户无法登陆。"
                ))
            }
        }
        //登录
        val loginResult = service.login(LoginDTO(
                email = vo.email,
                password = vo.password,
                ip = ipLocation.ip,
                ipLocation = ipLocation.getLocation()
        ))
        if (!loginResult.isSuccess){
            //登录失败
            return model.apply {
                viewName = "login"
                addObject(ResultDialogVO.NAME, ResultDialogVO(
                        title = "登录失败",
                        type = "info",
                        content = loginResult.msg
                ))
            }
        }
        val data = loginResult.data!!
        //服务器限制登录
        val loginLimit = config.getBoolean(ConfigInfo.WEB_LOGIN_LIMIT,false)
        if (loginLimit && CommonConst.USER_TYPE_NORMAL == data.type) {
            return model.apply {
                viewName = "login"
                addObject(ResultDialogVO.NAME,ResultDialogVO(
                        title = "登录失败",
                        type = ResultDialogVO.TYPE_INFO,
                        content = "服务器维护中，请稍后再试。"
                ))
            }
        }
        //写入会话信息
        val onlineExpire = config.getLong(ConfigInfo.USER_ONLINE_EXPIRE,30)
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${request.sessionID}"
        val sessionInfo = QYSessionInfo(
                id = data.id,
                email = data.email,
                nickname = data.nickname,
                type = data.type
        )
        template.opsForHash<String,String>().put(sessionKey,"info",sessionInfo.toJson())
        template.expire(sessionKey,onlineExpire,TimeUnit.MINUTES)
        //自动登录
        if (vo.autoLogin){
            val expire = config.getLong(ConfigInfo.USER_AUTOLOGIN_EXPIRE,30L)
            val token = idWorker.nextId()
            val autoLoginKey = "${Const.FUNC_AUTOLOGIN_REDIS_KET}:${token}"
            template.opsForValue().set(autoLoginKey,sessionInfo.toJson(),expire,TimeUnit.DAYS)
            response.addCookie(Cookie(Const.AUTOLOGIN_KEY,token.toString(16)).apply {
                this.path = "/"
                this.maxAge = expire.toInt() * 24 * 60 * 60
            })
        }
        response.sendRedirect("/")
        return null
    }
}