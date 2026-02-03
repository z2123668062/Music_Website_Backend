package org.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

// 用户信息表（个性化内容，一对一关联User，MyBatis-Plus注解）
@Data
@TableName("userInfo")
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id") // 数据库字段名，关联User表的id
    private Long userId;    // 外键：关联用户主表ID（核心）
    private String nickname; // 昵称（个性化，可重复）
    private String intro;    // 个人简介
    private String gender;   // 性别（男/女/未知）
    private String birthday; // 生日
    private String location; // 所在地
    // 后续可扩展：用户等级、个性签名、背景图URL等
    private Date createTime;
    private Date updateTime;
}
