package org.example.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.music.entity.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    //跟音乐访问层一样，不用写增删改查代码

}
