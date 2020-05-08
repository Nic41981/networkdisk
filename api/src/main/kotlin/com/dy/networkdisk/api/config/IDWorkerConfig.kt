package com.dy.networkdisk.api.config

enum class IDWorkerConfig constructor(
        val id: Long
) {

    WEB_MODULE(id = 0L),
    USER_MODULE(id = 1L),
    FILE_MODULE(id = 2L),
    UPLOAD_MODULE(id = 3L)

}