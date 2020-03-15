package com.dy.networkdisk.web.controller.user;

import com.dy.networkdisk.api.dto.user.RegisterInfoDTO;
import com.dy.networkdisk.api.user.UserService;
import com.dy.networkdisk.web.config.Const;
import com.dy.networkdisk.web.tool.StringUtil;
import com.dy.networkdisk.web.tool.KaptchaUtil;
import com.dy.networkdisk.web.vo.RegisterInfoVo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final KaptchaUtil kaptcha;

    @Reference
    private UserService service;

    @GetMapping("/user/login")
    public String getLoginPage(){
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
        String activeToken = service.getGuestsLock(info.getUsername());
        if (activeToken == null) {
            info.setUsername("");
            return registerError(model, info, "该用户名已注册!");
        }
        RegisterInfoDTO dto = new RegisterInfoDTO();
        dto.setUsername(info.getUsername());
        dto.setPassword(info.getPassword());
        dto.setEmail(info.getEmail());
        dto.setCreateTime(new Date());
        service.register(activeToken, dto);
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

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        service.getGuestsLock("123");
        return "success";
    }
}
