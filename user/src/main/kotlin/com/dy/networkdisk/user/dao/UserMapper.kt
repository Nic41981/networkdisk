package com.dy.networkdisk.user.dao

import com.dy.networkdisk.user.po.UserPO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import javax.annotation.PostConstruct

@Mapper
interface UserMapper {
    @PostConstruct
    fun init(): Int

    fun register(po: UserPO)
    fun login(email: String): UserPO?

    fun checkEmailExist(email: String): Int

    fun findTypeByEmail(email: String): String?
}