package org.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;          // 主键（核心关联ID，所有场景通用）

    private String username;  // 用户名（本地注册：必填且唯一；第三方登录：自动生成（如昵称+随机数）或后续完善，允许为空但后续可补）

    private String password;  // 密码（本地注册：必填且加密存储；第三方登录：可为空（因为第三方登录无需本地密码））

    private String mobile;    // 手机号（唯一，本地注册/绑定手机号时填写，允许为空）

    private String email;     // 邮箱（唯一，本地注册/绑定邮箱时填写，允许为空）

    private Integer status;   // 账号状态（0正常/1禁用，所有场景通用，默认0）

    private String avatar;    // 头像URL（本地注册：可选；第三方登录：直接获取第三方头像，允许为空）

    private Date createTime;  // 创建时间（所有场景通用，自动填充）

    private Date updateTime;  // 更新时间（所有场景通用，自动填充）
}
