package com.dy.networkdisk.web.tool

import com.dy.networkdisk.api.dto.dubbo.user.GuestsDTO
import com.dy.networkdisk.web.vo.RegisterInfoVo
import java.util.*

class BeanTransUtil {
    companion object{

        @JvmStatic
        fun trans(from: RegisterInfoVo,to: GuestsDTO):GuestsDTO{
            with(to){
                username = from.username
                password = from.password
                email = from.email
                registerDate = Date()
            }
            return to
        }
    }
}