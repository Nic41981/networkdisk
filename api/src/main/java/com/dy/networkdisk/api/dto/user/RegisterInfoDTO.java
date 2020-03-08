package com.dy.networkdisk.api.dto.user;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class RegisterInfoDTO implements Serializable {
    private String username;
    private String password;
    private String email;
    private Date createTime;
}
