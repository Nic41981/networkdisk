package com.dy.networkdisk.api.user;

import com.dy.networkdisk.api.dto.dubbo.user.GuestsDTO;

public interface UserService {

    String getGuestsLock(String username);

    void register(GuestsDTO guests);

    boolean hasLogin(String token);


}
