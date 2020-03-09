package com.dy.networkdisk.email.service.user;

import com.dy.networkdisk.api.dto.email.AccountActiveDTO;
import com.dy.networkdisk.email.config.MailSender;
import com.dy.networkdisk.email.tool.GsonTool;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import java.util.Date;

@Async
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActiveEmailServer {

    private final MailSender sender;
    private final TemplateEngine engine;

    @JmsListener(destination = "account_active")
    public void send(String message) throws MessagingException {
        AccountActiveDTO activeMsg = GsonTool.toObject(message,AccountActiveDTO.class);
        Context context = new Context();
        context.setVariable("activeURL",activeMsg.getActiveURL());
        context.setVariable("username",activeMsg.getUsername());
        context.setVariable("token",activeMsg.getToken());
        context.setVariable("createTime",new Date());
        String html = engine.process("ActiveAccountEmail",context);
        sender.byHtml("","激活账户",html);
    }
}
