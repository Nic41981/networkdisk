package com.dy.networkdisk.email.service.user

import com.dy.networkdisk.api.config.ConfigInfo
import com.dy.networkdisk.api.dto.email.ActiveEmailDTO
import com.dy.networkdisk.api.dto.email.RemoteLoginEmailDTO
import com.dy.networkdisk.api.email.UserEmailService
import com.dy.networkdisk.email.tool.ConfigUtil
import com.dy.networkdisk.email.tool.MailUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService
class UserEmailServiceImpl @Autowired constructor(
        private val engine: TemplateEngine,
        private val config: ConfigUtil,
        private val mail: MailUtil
): UserEmailService {

    override fun activeAccount(dto: ActiveEmailDTO){
        val signature = config.getString(ConfigInfo.MAIL_SIGNATURE, "青叶网盘")
        val context = Context().apply {
            setVariables(mapOf(
                    "user" to dto,
                    "signature" to signature,
                    "create_time" to Date()
            ))
        }
        val html = engine.process("ActiveAccountEmail", context)
        mail.sendByHtml(dto.email,"激活账户",html)
    }

    override fun remoteLogin(dto: RemoteLoginEmailDTO) {
        val signature = config.getString(ConfigInfo.MAIL_SIGNATURE, "青叶网盘")
        val context = Context().apply {
            setVariables(mapOf(
                    "msg" to dto,
                    "signature" to signature,
                    "create_time" to Date()
            ))
        }
        val html = engine.process("RemoteLoginEmail", context)
        mail.sendByHtml(dto.email, "异地登录", html)
    }
}