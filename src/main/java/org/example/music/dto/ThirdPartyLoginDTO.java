package org.example.music.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 第三方登录DTO（仅处理第三方登录，不涉及本地注册/登录）
 */
@Data
public class ThirdPartyLoginDTO {
    /**
     * 第三方授权码（必填，第三方平台返回的临时授权码，一次性有效）
     */
    @NotBlank(message = "第三方授权码不能为空")
    private String code;

    /**
     * 第三方平台类型（必填，wechat/qq/github）
     */
    @NotBlank(message = "第三方平台类型不能为空")
    private String thirdType;
}
