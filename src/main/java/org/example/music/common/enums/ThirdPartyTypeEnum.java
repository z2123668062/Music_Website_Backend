package org.example.music.common.enums;

import lombok.Getter;

@Getter
public enum ThirdPartyTypeEnum {
    WECHAT("wechat", "微信"),
    QQ("qq", "QQ"),
    GITHUB("github", "GitHub");

    private final String code;
    private final String desc;

    ThirdPartyTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据code获取枚举（方便后续校验）
    public static ThirdPartyTypeEnum getByCode(String code) {
        for (ThirdPartyTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
