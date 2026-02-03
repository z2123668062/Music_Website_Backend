package org.example.music.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义授权异常（用于封装JWT校验、Redis校验中的错误）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthException extends RuntimeException {
    // 可添加错误码（可选，便于前端统一处理）
    private Integer errorCode;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // getter/setter 省略
}