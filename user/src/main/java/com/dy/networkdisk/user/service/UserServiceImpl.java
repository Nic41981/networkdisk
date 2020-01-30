package com.dy.networkdisk.user.service;

import com.dy.networkdisk.api.user.UserService;
import org.apache.dubbo.config.annotation.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean hasLogin(String token) {
        return false;
    }
}
