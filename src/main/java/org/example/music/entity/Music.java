package org.example.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("music")
public class Music {
    @TableId(type = IdType.AUTO)  // 主键生成策略：AUTO（数据库自增）
    private Long id;

    private String title;

    // 歌手名
    private String artist;

    // 歌曲标签（如“流行”“摇滚”，用逗号分隔）
    private String tags;

    // 音乐文件存储路径
    private String filePath;

    // 上传时间
    private LocalDateTime uploadTime;
}
