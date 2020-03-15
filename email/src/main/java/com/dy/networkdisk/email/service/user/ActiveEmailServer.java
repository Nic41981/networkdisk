package com.dy.networkdisk.email.service.user;

import com.dy.networkdisk.api.config.UserConst;
import com.dy.networkdisk.api.dto.email.AccountActiveDTO;
import com.dy.networkdisk.email.tool.GsonTool;
import com.dy.networkdisk.email.tool.MailSenderTool;
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

    @JmsListener(destination = "email.account.active")
    public void send(String message) {
        AccountActiveDTO activeMsg = GsonTool.toObject(message, AccountActiveDTO.class);
        Context context = new Context();
        context.setVariable("activeURL",activeMsg.getActiveURL());
        context.setVariable("username",activeMsg.getUsername());
        context.setVariable("token",activeMsg.getToken());
        context.setVariable("signature", UserConst.mailSignature);
        context.setVariable("createTime",new Date());
        String html = engine.process("ActiveAccountEmail",context);
        MailSenderTool.sendByHtml(activeMsg.getEmail(),"激活账户",html);
    }
}
