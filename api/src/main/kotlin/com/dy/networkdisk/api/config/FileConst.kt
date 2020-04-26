package com.dy.networkdisk.api.config

class FileConst {
    enum class NodeType {
        FOLDER,
        FILE
    }

    enum class MimeType {
        ROOT,
        TEXT,
        UNKNOWN
    }

    enum class Status(val tip: String) {
        NORMAL("正常"),
        DELETE("已删除"),
        UPLOADING("上传中"),
        FAIL("异常")
    }
}