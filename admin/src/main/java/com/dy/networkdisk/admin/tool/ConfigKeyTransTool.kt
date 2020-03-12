package com.dy.networkdisk.admin.tool

import com.dy.networkdisk.admin.config.ConfigPropInfo
import com.dy.networkdisk.api.config.ConfigRedisKey

class ConfigKeyTransTool {
    companion object{
        private val transRedisMap = mapOf(
                ConfigPropInfo.WEB_TITLE.key to ConfigRedisKey.WEB_TITLE.key,
                ConfigPropInfo.WEB_ALLOW_REGISTER.key to ConfigRedisKey.WEB_ALLOW_REGISTER.key,
                ConfigPropInfo.WEB_VERIFICATION_CHARSET.key to ConfigRedisKey.WEB_VERIFICATION_CHARSET.key,
                ConfigPropInfo.WEB_VERIFICATION_LENGTH.key to ConfigRedisKey.WEB_VERIFICATION_LENGTH.key,
                ConfigPropInfo.WEB_VERIFICATION_BORDER.key to ConfigRedisKey.WEB_VERIFICATION_BORDER.key,
                ConfigPropInfo.WEB_VERIFICATION_BORDER_WIDTH.key to ConfigRedisKey.WEB_VERIFICATION_BORDER_WIDTH.key,
                ConfigPropInfo.WEB_VERIFICATION_FONT_COLOR.key to ConfigRedisKey.WEB_VERIFICATION_FONT_COLOR.key,
                ConfigPropInfo.WEB_VERIFICATION_FONT_SIZE.key to ConfigRedisKey.WEB_VERIFICATION_FONT_SIZE.key,
                ConfigPropInfo.WEB_VERIFICATION_BACKGROUND_COLOR_FROM.key to ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_FROM.key,
                ConfigPropInfo.WEB_VERIFICATION_BACKGROUND_COLOR_TO.key to ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_TO.key,
                ConfigPropInfo.WEB_VERIFICATION_EXPIRE.key to ConfigRedisKey.WEB_VERIFICATION_EXPIRE.key,
                ConfigPropInfo.USER_GUESTS_EXPIRE.key to ConfigRedisKey.USER_GUESTS_EXPIRE.key,
                ConfigPropInfo.USER_GUESTS_ACTIVE_HOST.key to ConfigRedisKey.USER_GUESTS_ACTIVE_HOST.key,
                ConfigPropInfo.MAIL_SIGNATURE.key to ConfigRedisKey.MAIL_SIGNATURE.key
        )

        private val transPropMap = mapOf(
                ConfigRedisKey.WEB_TITLE.key to ConfigPropInfo.WEB_TITLE.key,
                ConfigRedisKey.WEB_ALLOW_REGISTER.key to ConfigPropInfo.WEB_ALLOW_REGISTER.key,
                ConfigRedisKey.WEB_VERIFICATION_CHARSET.key to ConfigPropInfo.WEB_VERIFICATION_CHARSET.key,
                ConfigRedisKey.WEB_VERIFICATION_LENGTH.key to ConfigPropInfo.WEB_VERIFICATION_LENGTH.key,
                ConfigRedisKey.WEB_VERIFICATION_BORDER.key to ConfigPropInfo.WEB_VERIFICATION_BORDER.key,
                ConfigRedisKey.WEB_VERIFICATION_BORDER_WIDTH.key to ConfigPropInfo.WEB_VERIFICATION_BORDER_WIDTH.key,
                ConfigRedisKey.WEB_VERIFICATION_FONT_COLOR.key to ConfigPropInfo.WEB_VERIFICATION_FONT_COLOR.key,
                ConfigRedisKey.WEB_VERIFICATION_FONT_SIZE.key to ConfigPropInfo.WEB_VERIFICATION_FONT_SIZE.key,
                ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_FROM.key to ConfigPropInfo.WEB_VERIFICATION_BACKGROUND_COLOR_FROM.key,
                ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_TO.key to ConfigPropInfo.WEB_VERIFICATION_BACKGROUND_COLOR_TO.key,
                ConfigRedisKey.WEB_VERIFICATION_EXPIRE.key to ConfigPropInfo.WEB_VERIFICATION_EXPIRE.key,
                ConfigRedisKey.USER_GUESTS_EXPIRE.key to ConfigPropInfo.USER_GUESTS_EXPIRE.key,
                ConfigRedisKey.USER_GUESTS_ACTIVE_HOST.key to ConfigPropInfo.USER_GUESTS_ACTIVE_HOST.key,
                ConfigRedisKey.MAIL_SIGNATURE.key to ConfigPropInfo.MAIL_SIGNATURE.key
        )
        
        @JvmStatic
        fun toRedisKey(propKey: String):String?{
            return this.transRedisMap[propKey]
        }

        @JvmStatic
        fun toPropKey(redisKey: String):String?{
            return this.transPropMap[redisKey]
        }
    }
}