package org.example.music.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    //私有构造方法
    private Result() {}
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //成功返回（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200,"操作成功",data);
    }

    //成功返回（不带数据）
    public static <T> Result<T> success() {
        return new Result<>(200,"操作成功，不带数据",null);
    }

    //返回失败（自定义返回失败状态码和信息）
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code,msg,null);
    }

    //返回失败（通用）
    public static <T> Result<T> fail() {
        return fail(500,"服务器内部错误");
    }
}
