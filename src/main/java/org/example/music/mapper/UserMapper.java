package org.example.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.music.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问层（Mapper）
 * 继承MyBatis-Plus的BaseMapper，自动获得增删改查基础方法
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    //跟音乐访问层一样，不用写增删改查代码
    //可以加自定义查询方法
    /**
     * 根据邮箱查询用户
     * @param email 邮箱地址
     * @return 对应的用户对象（无则返回 null）
     */
    @Select("SELECT * FROM user WHERE email = #{email}") // 注意：表名 user 请替换为你的实际表名
    User selectUserByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * @param mobile 手机号
     * @return 对应的用户对象（无则返回 null）
     */
    @Select("SELECT * FROM user WHERE mobile = #{mobile}") // 注意：表名 user 请替换为你的实际表名
    User selectUserByMobile(@Param("mobile") String mobile);

    //根据用户名查用户
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectUserByUsername(@Param("username") String username);

    //根据模糊账号查用户
    @Select("SELECT * FROM user WHERE username=#{account} or moblie =#{account} or email=#{account}")
    User selectUserByAccount(@Param("account") String account);

}
