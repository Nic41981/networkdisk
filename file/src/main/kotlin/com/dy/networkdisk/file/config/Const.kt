package com.dy.networkdisk.file.config

class Const {
    companion object{

        /**
         * 文件夹重名锁
         * 锁结构:主键:用户ID:父文件夹ID
         */
        const val LOCK_FOLDER_NAME = "QYDisk:lock:folder:name"

        /**
         * 文件重名锁
         * 锁结构:主键：用户ID：父文件夹ID
         */
        const val LOCK_FILE_NAME = "QYDisk:lock:file:name"
    }
}