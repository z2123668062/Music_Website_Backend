package org.example.music.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 第三方绑定本地账号DTO（可选，首次第三方登录后完善信息用）
 */
@Data
public class ThirdPartyBindDTO {
    /**
     * 用户名（可选，不填则自动生成第三方昵称+随机数）
     */
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名仅支持字母、数字和下划线")
    private String username;

    /**
     * 密码（可选，不填则本地密码为空，无法通过密码登录）
     */
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 手机号（可选，唯一，用于后续绑定/找回密码）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 邮箱（可选，唯一，用于后续绑定/找回密码）
     */
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 第三方平台类型（必填，wechat/qq/github，前端暂存后传递）
     */
    @NotBlank(message = "第三方平台类型不能为空")
    private String thirdType;

    /**
     * 第三方用户唯一标识（必填，前端暂存后传递）
     */
    @NotBlank(message = "第三方用户标识不能为空")
    private String thirdOpenId;
}