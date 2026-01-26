package org.example.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.music.entity.Music;
import org.springframework.stereotype.Repository;

/**
 * 音乐数据访问层（Mapper）
 * 继承MyBatis-Plus的BaseMapper，自动获得增删改查基础方法
 */
@Repository  // 标注为持久层组件，让Spring扫描到并管理（可选，但加了更规范）
public interface MusicMapper extends BaseMapper<Music> {

    // v1.0 无需手写任何方法！
    // BaseMapper<Music> 已经内置了以下核心方法：
    // 1. insert(Music music)：新增音乐
    // 2. selectList(Wrapper<Music> queryWrapper)：查询音乐列表
    // 3. selectById(Long id)：根据ID查询单首音乐
    // 4. updateById(Music music)：根据ID更新音乐
    // 5. deleteById(Long id)：根据ID删除音乐
    // 这些完全满足v1.0的数据库操作需求
}