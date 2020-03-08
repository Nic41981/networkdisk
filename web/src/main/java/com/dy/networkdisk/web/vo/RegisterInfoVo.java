package com.dy.networkdisk.web.vo;

import lombok.Data;

@Data
public class RegisterInfoVo {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String verificationCode;
}
