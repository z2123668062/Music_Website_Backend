package org.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_third_party")
public class UserThirdParty {
    @TableId(type = IdType.AUTO)
    private Long id;          // 关联表主键

    private Long userId;      // 关联本地用户主表的id（核心关联字段，不能为空）

    private String thirdType; // 第三方平台类型（如wechat/qq/github，不能为空，区分不同第三方）

    private String thirdOpenId; // 第三方平台用户唯一标识（如微信openid/unionid，不能为空，唯一标识第三方平台的用户）

    private String thirdNickname; // 第三方平台昵称（可选，用于备份）

    private String thirdAvatar;   // 第三方平台头像（可选，用于备份）

    private Date bindTime;    // 绑定时间（自动填充，记录用户与第三方平台的绑定时间）
}
