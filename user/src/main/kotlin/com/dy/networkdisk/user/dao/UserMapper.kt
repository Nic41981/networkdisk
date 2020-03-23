package com.dy.networkdisk.user.dao

import com.dy.networkdisk.user.po.UserPO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    fun init(): Int
    fun register(@Param("po") po: UserPO?): Boolean
    fun checkEmailExist(email: String?): Int
    fun findUserByEmail(email: String): UserPO
}