package com.dy.networkdisk.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigRedisKey {
    WEB_TITLE("QYDisk::config::web::title"),
    WEB_ALLOW_REGISTER("QYDisk::config::web::register::allow"),

    WEB_VERIFICATION_CHARSET("QYDisk::config::web::verification::charset"),
    WEB_VERIFICATION_LENGTH("QYDisk::config::web::verification::length"),
    WEB_VERIFICATION_BORDER("QYDisk::config::web::verification::border"),
    WEB_VERIFICATION_BORDER_WIDTH("QYDisk::config::web::verification::border::width"),
    WEB_VERIFICATION_FONT_COLOR("QYDisk::config::web::verification::font::color"),
    WEB_VERIFICATION_FONT_SIZE("QYDisk::config::web::verification::font::size"),
    WEB_VERIFICATION_BACKGROUND_COLOR_FROM("QYDisk::config::web::verification::background::color::from"),
    WEB_VERIFICATION_BACKGROUND_COLOR_TO("QYDisk::config::web::verification::background::color::to"),
    WEB_VERIFICATION_EXPIRE("QYDisk::config::web::verification::expire"),

    USER_GUESTS_EXPIRE("QYDisk::config::user::guests::expire"),
    USER_GUESTS_ACTIVE_HOST("QYDisk::config::user::guests::active::host"),

    MAIL_SIGNATURE("QYDisk::config::mail::signature");

    private String key;
}
