package org.example.music.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendCodeDTO {
    @NotBlank(message = "验证类型不能为空")
    @Pattern(regexp = "email|phone", message = "验证类型必须是email或phone")
    private String verifyType;

    @NotBlank(message = "验证账号不能为空")
    private String verifyAccount; // 邮箱或手机号
}