package com.dy.networkdisk.web.controller.user;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.api.dto.user.GuestsDTO;
import com.dy.networkdisk.api.user.UserService;
import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.tool.BeanTransUtil;
import com.dy.networkdisk.web.tool.ConfigUtil;
import com.dy.networkdisk.web.tool.StringUtil;
import com.dy.networkdisk.web.tool.KaptchaUtil;
import com.dy.networkdisk.web.vo.RegisterInfoVo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final KaptchaUtil kaptcha;
    private final ConfigUtil config;

    @Reference
    private UserService service;

    @GetMapping("/user/login")
    public String getLoginPage(ModelAndView model){
        model.setViewName("login");
        boolean allowRegister = config.getBoolean(ConfigRedisKey.WEB_ALLOW_REGISTER,Boolean.FALSE);
        model.addObject("allow_register",allowRegister);
        return "login";
    }

    @PostMapping("/user/login")
    public ModelAndView submitLogin(ModelAndView model){
        model.setViewName("login");
        model.addObject("result","功能暂未开放!");
        return model;
    }

    @GetMapping("/user/register")
    public String getRegisterPage(){
        return "register";
    }

    @PostMapping("/user/register")
    public ModelAndView submitRegister(HttpServletRequest request,ModelAndView model, RegisterInfoVo info) {
        String token = (String) request.getAttribute(Const.ONLINE_TOKEN_KEY);
        String ip = (String) request.getAttribute(Const.IP_KEY);
        if (StringUtil.isNull(info.getUsername()) || StringUtil.inLengthRange(info.getUsername(),2,20)) {
            info.setUsername("");
            return registerError(model, info, "用户名长度不正确（2-20）");
        }
        if (!info.getUsername().matches("^[\\w\\u4e00-\\u9fa5]+$")) {
            info.setUsername("");
            return registerError(model, info, "用户名包含非法字符（数字、字母、下划线和汉字）");
        }
        if (StringUtil.isNull(info.getPassword()) || StringUtil.inLengthRange(info.getPassword(),5,20)) {
            info.setPassword("");
            info.setConfirmPassword("");
            return registerError(model, info, "密码长度不正确（5-20）");
        }
        if (!info.getPassword().equals(info.getConfirmPassword())) {
            info.setPassword("");
            info.setConfirmPassword("");
            return registerError(model, info, "密码不一致");
        }
        if (StringUtil.isNull(info.getEmail())) {
            info.setEmail("");
            return registerError(model, info, "邮箱不可以为空");
        }
        if (kaptcha.check(token,info.getVerificationCode())) {
            info.setVerificationCode("");
            return registerError(model, info, "验证码错误");
        }
        String guestsLock = service.getGuestsLock(info.getUsername());
        if (guestsLock == null) {
            info.setUsername("");
            return registerError(model, info, "该用户名已注册!");
        }
        GuestsDTO guests = BeanTransUtil.trans(info,new GuestsDTO());
        guests.setIp(ip);
        guests.setLock(guestsLock);
        service.register(guests);
        model.setViewName("register");
        model.addObject("info", info);
        model.addObject("result", "功能暂未开放");
        return model;
    }

    private ModelAndView registerError(ModelAndView model,RegisterInfoVo info,String result){
        model.setViewName("register");
        model.addObject("info",info);
        model.addObject("result",result);
        return model;
    }
}
