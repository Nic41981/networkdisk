package com.dy.networkdisk.api.dto.email;

import lombok.Data;

@Data
public class AccountActiveDTO {
    private String activeURL;
    private String username;
    private String token;
}
