package org.example.music.service;

import org.example.music.dto.SendCodeDTO;

public interface VerifyCodeService {
    //实现验证码发送服务，返回布尔类型
    Boolean verifyCode(SendCodeDTO sendCodeDTO);
}
