package org.example.music.vo;

import lombok.Data;

/**
 * 登录响应VO（向前端返回登录结果）
 */
@Data
public class LoginResponseVO {
    private Long userId; // 用户主键ID（核心关联字段）
    private String username; // 用户名/昵称
    private String avatar; // 头像URL
    private String token; // 登录令牌（JWT，前端存储用于后续接口认证）
    private String message; // 提示信息（如「首次登录，建议完善手机号」）
}
