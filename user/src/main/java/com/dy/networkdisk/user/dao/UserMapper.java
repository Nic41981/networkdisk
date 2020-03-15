package com.dy.networkdisk.user.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    boolean register();
    int checkUsernameExist(String username);
}
