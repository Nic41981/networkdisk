package com.dy.networkdisk.api.config

class CommonConst {
    companion object{
        /*普通用户*/
        const val USER_TYPE_NORMAL = "normal"

        /*用户登录和会话信息*/
        const val FUNC_SESSION_REDIS_KEY = "QYDisk:session"

        /*文件夹类型*/
        const val FILE_NODE_TYPE_FOLDER = "folder"

        /*文件状态正常*/
        const val FILE_STATUS_NORMAL = "正常"
    }
}