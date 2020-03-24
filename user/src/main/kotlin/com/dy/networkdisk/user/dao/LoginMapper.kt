package com.dy.networkdisk.user.dao

import com.dy.networkdisk.user.po.LoginPO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface LoginMapper {
    fun insert(po: LoginPO)
}