package com.dy.networkdisk.user.config;

class Const {
    companion object {
        //用户状态
        const val USER_STATUS_NORMAL = 0
        const val USER_STATUS_DELETE = 1

        //用户类型
        const val USER_TYPE_ROOT = "root"
        const val USER_TYPE_ADMIN = "admin"
        const val USER_TYPE_NORMAL = "normal"
        const val USER_TYPE_GUESTS = "guests"
        const val USER_TYPE_UNKNOWN = "unknown"

        /*游客账号信息*/
        const val FUNC_GUESTS_REDIS_KEY = "QYDisk:user:guests"

        /*用户封禁信息*/
        const val FUNC_USER_LOCK_REDIS_KEY = "QYDisk:user:lock"

        /*用户自动登录信息*/
        const val FUNC_USER_AUTOLOGIN_REDIS_KET = "QYDisk:user:autologin"
    }
 }
