package com.dy.networkdisk.email.service.user;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.api.config.QueueConst;
import com.dy.networkdisk.api.dto.mq.email.AccountActiveDTO;
import com.dy.networkdisk.email.tool.ConfigUtil;
import com.dy.networkdisk.email.tool.GsonTool;
import com.dy.networkdisk.email.tool.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Date;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActiveEmailServer {

    private final TemplateEngine engine;

    @JmsListener(destination = QueueConst.MAIL_ACCOUNT_ACTIVE)
    public void send(String message) {
        AccountActiveDTO activeMsg = GsonTool.toObject(message, AccountActiveDTO.class);
        ConfigUtil config = ConfigUtil.getInstance();
        String signature = config.getString(ConfigRedisKey.MAIL_SIGNATURE,"青叶网盘");
        Context context = new Context();
        context.setVariable("username",activeMsg.getUsername());
        context.setVariable("email",activeMsg.getEmail());
        context.setVariable("register_time",activeMsg.getRegisterDate());
        context.setVariable("register_ip",activeMsg.getIp());
        context.setVariable("activeURL",activeMsg.getActiveURL());
        context.setVariable("token",activeMsg.getToken());
        context.setVariable("signature", signature);
        context.setVariable("create_time",new Date());
        String html = engine.process("ActiveAccountEmail",context);
        MailUtil.sendByHtml(activeMsg.getEmail(),"激活账户",html);
    }
}
