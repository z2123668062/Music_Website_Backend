package org.example.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权校验通过后的用户信息封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
}