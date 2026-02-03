package org.example.music.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 本地用户注册DTO（仅处理本地注册，不涉及第三方）
 */
@Data
public class LocalRegisterDTO {
    /**
     * 用户名（必填，长度4-20，仅字母、数字、下划线，唯一）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名仅支持字母、数字和下划线")
    private String username;

    /**
     * 密码（必填，长度6-32，支持任意字符）
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 验证方式（必填，email/phone，对应本地验证方式）
     */
    @NotBlank(message = "验证方式不能为空")
    private String verifyType;

    /**
     * 验证账号（必填，email/phone的账号，用来获取验证码）
     */
    @NotBlank
    private String verifyAccount;

    /**
     * 验证码（必填，6位数字）
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifyCode;
}