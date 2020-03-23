package com.dy.networkdisk.email.service.user

import com.dy.networkdisk.api.config.ConfigRedisKey
import com.dy.networkdisk.api.config.QueueConst
import com.dy.networkdisk.api.dto.mq.email.UserActiveEmailDTO
import com.dy.networkdisk.email.tool.ConfigUtil
import com.dy.networkdisk.email.tool.MailUtil
import com.dy.networkdisk.email.tool.GsonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*

@Service
class UserActiveEmailService @Autowired constructor(
        private val engine: TemplateEngine
) {
    @JmsListener(destination = QueueConst.activeMailQueue)
    fun send(message: String) {
        val user = GsonUtil.fromJson<UserActiveEmailDTO>(message, UserActiveEmailDTO::class.java) ?: return
        val config = ConfigUtil.getInstance()
        val signature = config.getString(ConfigRedisKey.MAIL_SIGNATURE, "青叶网盘")
        val context = Context()
        context.setVariable("user", user)
        context.setVariable("signature", signature)
        context.setVariable("create_time", Date())
        val html = engine.process("ActiveAccountEmail", context)
        MailUtil.sendByHtml(user.email, "激活账户", html)
    }
}