package com.dy.networkdisk.web.config

import com.dy.networkdisk.api.config.ConfigRedisKey

enum class KaptchaKeyInfo(val kaptchaKey: String, val redisKey: ConfigRedisKey) {
    BORDER("kaptcha.border",ConfigRedisKey.WEB_VERIFICATION_BORDER),
    BORDER_COLOR("kaptcha.border.color",ConfigRedisKey.WEB_VERIFICATION_BORDER_COLOR),
    BORDER_WIDTH("kaptcha.border.thickness",ConfigRedisKey.WEB_VERIFICATION_BORDER_WIDTH),
    FONT_COLOR("kaptcha.textproducer.font.color",ConfigRedisKey.WEB_VERIFICATION_FONT_COLOR),
    FONT_SIZE("kaptcha.textproducer.font.size",ConfigRedisKey.WEB_VERIFICATION_FONT_SIZE),
    FONT_SPACE("kaptcha.textproducer.char.space",ConfigRedisKey.WEB_VERIFICATION_FONT_SPACE),
    NOISE_IMPL("kaptcha.noise.impl",ConfigRedisKey.WEB_VERIFICATION_NOISE_IMPL),
    NOISE_COLOR("kaptcha.noise.color",ConfigRedisKey.WEB_VERIFICATION_NOISE_COLOR),
    STYLE_IMPL("kaptcha.obscurificator.impl",ConfigRedisKey.WEB_VERIFICATION_STYLE_IMPL),
    BACKGROUND_COLOR_FROM("kaptcha.background.clear.from",ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_FROM),
    BACKGROUND_COLOR_TO("kaptcha.background.clear.to",ConfigRedisKey.WEB_VERIFICATION_BACKGROUND_COLOR_TO);
}