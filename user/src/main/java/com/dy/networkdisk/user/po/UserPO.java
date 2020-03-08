package com.dy.networkdisk.user.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserPO {
    private long id;
    private String username;
    private String password;
    private String email;
    private Date createTime;
    private Date activeTime;

    /**
     * -1:账号已删除
     * 0：账号待激活
     * 1：账号正常
     */
    private byte state;
}
