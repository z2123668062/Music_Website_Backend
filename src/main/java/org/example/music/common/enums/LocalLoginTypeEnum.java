package org.example.music.common.enums;

import lombok.Getter;

/**
 * 本地登录类型枚举
 */
@Getter
public enum LocalLoginTypeEnum {
    PASSWORD("password", "密码登录"),
    PHONE("phone", "手机号验证码登录"),
    EMAIL("email", "邮箱验证码登录");

    private final String code;
    private final String desc;

    LocalLoginTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LocalLoginTypeEnum getByCode(String code) {
        for (LocalLoginTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
