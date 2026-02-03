package org.example.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.music.entity.UserThirdParty;
import org.springframework.stereotype.Repository;

@Repository
public interface UserThirdPartyMapper extends BaseMapper<UserThirdParty> {
    //跟音乐访问层一样，不用写增删改查代码

}
