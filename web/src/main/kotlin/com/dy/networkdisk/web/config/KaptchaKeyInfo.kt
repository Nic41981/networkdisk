package com.dy.networkdisk.web.config

import com.dy.networkdisk.api.config.ConfigInfo

enum class KaptchaKeyInfo(val kaptchaKey: String, val info: ConfigInfo) {
    BORDER("kaptcha.border",ConfigInfo.WEB_VERIFICATION_BORDER),
    BORDER_COLOR("kaptcha.border.color",ConfigInfo.WEB_VERIFICATION_BORDER_COLOR),
    BORDER_WIDTH("kaptcha.border.thickness",ConfigInfo.WEB_VERIFICATION_BORDER_WIDTH),
    FONT_COLOR("kaptcha.textproducer.font.color",ConfigInfo.WEB_VERIFICATION_FONT_COLOR),
    FONT_SIZE("kaptcha.textproducer.font.size",ConfigInfo.WEB_VERIFICATION_FONT_SIZE),
    FONT_SPACE("kaptcha.textproducer.char.space",ConfigInfo.WEB_VERIFICATION_FONT_SPACE),
    NOISE_IMPL("kaptcha.noise.impl",ConfigInfo.WEB_VERIFICATION_NOISE_IMPL),
    NOISE_COLOR("kaptcha.noise.color",ConfigInfo.WEB_VERIFICATION_NOISE_COLOR),
    STYLE_IMPL("kaptcha.obscurificator.impl",ConfigInfo.WEB_VERIFICATION_STYLE_IMPL),
    BACKGROUND_COLOR_FROM("kaptcha.background.clear.from",ConfigInfo.WEB_VERIFICATION_BACKGROUND_COLOR_FROM),
    BACKGROUND_COLOR_TO("kaptcha.background.clear.to",ConfigInfo.WEB_VERIFICATION_BACKGROUND_COLOR_TO);
}