package com.dy.networkdisk.user.config;

class Const {
    companion object {
        const val USER_STATUS_NORMAL = 0;
        const val USER_STATUS_DELETE = -1;
        const val USER_STATUS_LOCKED = 1;


        /*临时(未激活)账号信息*/
        const val FUNC_GUESTS_REDIS_KEY = "QYDisk:user:guests";
        /*临时(未激活)账号用户名锁(邮件激活token)*/
        const val FUNC_GUESTS_LOCK_REDIS_KEY = "QYDisk:user:guests:lock";
    }
 }
