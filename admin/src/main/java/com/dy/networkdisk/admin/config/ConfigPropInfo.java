package com.dy.networkdisk.admin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigPropInfo {
    WEB_TITLE("web.title", "青叶网盘"),
    WEB_ALLOW_REGISTER("web.register.allow", "true"),

    WEB_VERIFICATION_CHARSET("web.verification.charset", Const.DEFAULT_VERIFICATION_CHARSET),
    WEB_VERIFICATION_EXPIRE("web.verification.expire","10"),
    WEB_VERIFICATION_LENGTH("web.verification.length", "2"),
    WEB_VERIFICATION_CASE_IGNORE("web.verification.ignoreCase","false"),
    WEB_VERIFICATION_BORDER("web.verification.border", "no"),
    WEB_VERIFICATION_BORDER_COLOR("web.verification.border.color","black"),
    WEB_VERIFICATION_BORDER_WIDTH("web.verification.border.width", "0"),
    WEB_VERIFICATION_FONT_COLOR("web.verification.font.color", "black"),
    WEB_VERIFICATION_FONT_SIZE("web.verification.font.size","20"),
    WEB_VERIFICATION_FONT_SPACE("web.verification.font.space","10"),
    WEB_VERIFICATION_NOISE_IMPL("web.verification.noise.impl","com.google.code.kaptcha.impl.NoNoise"),
    WEB_VERIFICATION_NOISE_COLOR("web.verification.noise.color","black"),
    WEB_VERIFICATION_STYLE_IMPL("web.verification.style.impl","com.google.code.kaptcha.impl.ShadowGimpy"),
    WEB_VERIFICATION_BACKGROUND_COLOR_FROM("web.verification.background.color.from","darkGray"),
    WEB_VERIFICATION_BACKGROUND_COLOR_TO("web.verification.background.color.to","gray"),

    USER_GUESTS_EXPIRE("user.guests.expire","12"),
    USER_GUESTS_ACTIVE_HOST("user.guests.active.host","127.0.0.1:8080"),

    MAIL_SIGNATURE("mail.signature","青叶网盘");

    private String key;
    private String defaultValue;
}
