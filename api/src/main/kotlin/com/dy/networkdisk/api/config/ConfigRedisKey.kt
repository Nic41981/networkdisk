package com.dy.networkdisk.api.config;

enum class ConfigRedisKey(val key:String) {
    WEB_TITLE("QYDisk:config:web:title"),
    WEB_REGISTER_ALLOW("QYDisk:config:web:register:allow"),
    WEB_REGISTER_ONLY_INVITE("QYDisk:config:web:register:onlyInvite"),
    WEB_LOGIN_LIMIT("QYDisk:config:web:login:limit"),

    WEB_VERIFICATION_CHARSET("QYDisk:config:web:verification:charset"),
    WEB_VERIFICATION_EXPIRE("QYDisk:config:web:verification:expire"),
    WEB_VERIFICATION_LENGTH("QYDisk:config:web:verification:length"),
    WEB_VERIFICATION_CASE_IGNORE("QYDisk:config:web:verification:ignoreCase"),
    WEB_VERIFICATION_BORDER("QYDisk:config:web:verification:border"),
    WEB_VERIFICATION_BORDER_COLOR("QYDisk:config:web:verification:border:color"),
    WEB_VERIFICATION_BORDER_WIDTH("QYDisk:config:web:verification:border:width"),
    WEB_VERIFICATION_FONT_COLOR("QYDisk:config:web:verification:font:color"),
    WEB_VERIFICATION_FONT_SIZE("QYDisk:config:web:verification:font:size"),
    WEB_VERIFICATION_FONT_SPACE("QYDisk:config:web:verification:font:space"),
    WEB_VERIFICATION_NOISE_IMPL("QYDisk:config:web:verification:noise:impl"),
    WEB_VERIFICATION_NOISE_COLOR("QYDisk:config:web:verification:noise:color"),
    WEB_VERIFICATION_STYLE_IMPL("QYDisk:config:web:verification:style:impl"),
    WEB_VERIFICATION_BACKGROUND_COLOR_FROM("QYDisk:config:web:verification:background:color:from"),
    WEB_VERIFICATION_BACKGROUND_COLOR_TO("QYDisk:config:web:verification:background:color:to"),

    USER_GUESTS_EXPIRE("QYDisk:config:user:guests:expire"),

    MAIL_SIGNATURE("QYDisk:config:mail:signature");
}
