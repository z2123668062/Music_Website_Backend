package org.example.music.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 本地用户登录DTO（仅处理本地登录，不涉及第三方）
 */
@Data
public class LocalLoginDTO {
    /**
     * 用户名/手机号/邮箱（必填，用于查询本地用户）
     * 备注：简化设计，统一用一个字段接收，后端自行判断类型
     */
    @NotBlank(message = "登录账号不能为空")
    private String account;

    /**
     * password/verify
     * 备注：简化设计，统一用一个字段接收，后端自行判断类型
     */
    @NotBlank
    private String loginType;

    /**
     * 密码（可选，密码登录时必填）
     */
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 验证方式（可选，验证码登录时必填，email/phone）
     */
    private String verifyType;

    /**
     * 验证码（可选，验证码登录时必填，6位数字）
     */
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifyCode;

    /**
     * 记住我（可选，默认false）
     */
    private Boolean rememberMe = false;
}
