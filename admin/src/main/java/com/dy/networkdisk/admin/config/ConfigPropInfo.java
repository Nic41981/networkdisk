package com.dy.networkdisk.admin.config;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigPropInfo {
    WEB_TITLE(
            "web.title",
            "青叶网盘",
            ConfigRedisKey.WEB_TITLE.getKey()
    ),
    WEB_ALLOW_REGISTER(
            "web.register.allow",
            "true",
            ConfigRedisKey.WEB_ALLOW_REGISTER.getKey()
    ),

    WEB_VERIFICATION_CHARSET(
            "web.verification.charset",
            Const.DEFAULT_VERIFICATION_CHARSET,
            ConfigRedisKey.WEB_VERIFICATION_CHARSET.getKey()
    ),
    WEB_VERIFICATION_LENGTH(
            "web.verification.length",
            "2",
            ConfigRedisKey.WEB_VERIFICATION_LENGTH.getKey()
    ),
    WEB_VERIFICATION_BORDER(
            "web.verification.border",
            "no",
            ConfigRedisKey.WEB_VERIFICATION_BORDER.getKey()
    ),
    WEB_VERIFICATION_BORDER_WIDTH(
            "web.verification.border.width",
            "0",
            ConfigRedisKey.WEB_VERIFICATION_BORDER_WIDTH.getKey()
    ),
    WEB_VERIFICATION_FONT_COLOR(
            "web.verification.font.color",
            "black",
            ConfigRedisKey.WEB_VERIFICATION_FONT_COLOR.getKey()
    ),
    WEB_VERIFICATION_FONT_SIZE(
            "web.verification.font.size",
            "20",
            ConfigRedisKey.WEB_VERIFICATION_FONT_SIZE.getKey()
    ),
    WEB_VERIFICATION_BACKGROUND_COLOR_FROM(
            "web.verification.background.color.from",
            "darkGrag",
            ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_FROM.getKey()
    ),
    WEB_VERIFICATION_BACKGROUND_COLOR_TO(
            "web.verification.background.color.to",
            "gray",
            ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_TO.getKey()
    ),
    WEB_VERIFICATION_EXPIRE(
            "web.verification.expire",
            "10",
            ConfigRedisKey.WEB_VERIFICATION_EXPIRE.getKey()
    ),

    USER_GUESTS_EXPIRE(
            "user.guests.expire",
            "720",
            ConfigRedisKey.USER_GUESTS_EXPIRE.getKey()
    ),
    USER_GUESTS_ACTIVE_HOST(
            "user.guests.active.host",
            "127.0.0.1:8080",
            ConfigRedisKey.USER_GUESTS_ACTIVE_HOST.getKey()
    ),

    MAIL_SIGNATURE(
            "mail.signature",
            "青叶网盘",
            ConfigRedisKey.MAIL_SIGNATURE.getKey()
    );

    private String propKey;
    private String defaultValue;
    private String redisKey;
}
