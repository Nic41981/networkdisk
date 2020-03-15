package com.dy.networkdisk.api.dto.email;

import lombok.Data;

import java.util.Date;

@Data
public class AccountActiveDTO {
    private String username;
    private String email;
    private String activeURL;
    private String token;
    private String ip;
    private Date registerDate;
}
