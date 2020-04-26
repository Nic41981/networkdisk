package com.dy.networkdisk.upload.service

import com.dy.networkdisk.api.upload.UploadService
import org.springframework.stereotype.Service
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService(protocol = ["hessian"])
class UploadServiceImpl: UploadService {

    override fun upload() {
        //todo 检查缓存记录
        //     创建记录
        //todo 记录分块
        //todo 存储分块
        //todo 检查分块是否上传完成
        //     加密文件
        //     跟新数据库
    }

}