package com.dy.networkdisk.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("login")
    public String getLoginPage(){
        return "login";
    }

    @PostMapping("login")
    public ModelAndView tryLogin(ModelAndView model){
        model.setViewName("login");
        model.addObject("result","功能暂未开放!");
        return model;
    }
}
