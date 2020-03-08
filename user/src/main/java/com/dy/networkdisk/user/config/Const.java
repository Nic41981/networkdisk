package com.dy.networkdisk.user.config;

public class Const {
    /*图形验证码信息*/
    public static final String FUNC_VERIFICATION_REDIS_KEY = "QYDisk:user:verification";
    /*临时(未激活)账号信息*/
    public static final String FUNC_TEMP_ACCOUNT_REDIS_KEY = "QYDisk:user:tmp_account";
    /*临时(未激活)账号用户名锁(邮件激活token)*/
    public static final String FUNC_TEMP_ACCOUNT_LOCK_REDIS_KEY = "QYDisk:user:tmp_account:lock";
 }
