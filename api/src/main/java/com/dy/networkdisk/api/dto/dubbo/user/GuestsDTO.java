package com.dy.networkdisk.api.dto.dubbo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GuestsDTO implements Serializable {
    private String username;
    private String password;
    private String email;
    private String lock;
    private String ip;
    private Date registerDate;
}
