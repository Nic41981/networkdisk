package com.dy.networkdisk.api.user;

import com.dy.networkdisk.api.dto.user.RegisterInfoDTO;

public interface UserService {

    String tryLockUsername(String username);

    void register(String token,RegisterInfoDTO RegisterInfo);

    boolean hasLogin(String token);


}
