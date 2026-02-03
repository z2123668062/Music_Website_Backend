package org.example.music.service;


import org.example.music.dto.LocalLoginDTO;
import org.example.music.dto.LocalRegisterDTO;
import org.example.music.dto.ThirdPartyLoginDTO;
import org.example.music.vo.LoginResponseVO;

public interface UserService {
    /*
      用户注册功能（接收用户信息存入数据库）
      用户登录功能（校验用户信息，实现权限控制）
      用户登出功能（暂时想的是用Redis）
      修改个性信息（直接根据ID查UserInfo表）
      修改敏感信息（多做一层信息校验，然后改密码）
      查询信息（查询用户表）
     */

    //1.用户注册
    void localRegister(LocalRegisterDTO userRegisterDTO);

    //2.用户登录(本地登录，第三方登录）
    LoginResponseVO localLogin(LocalLoginDTO localLoginDTO);

    //LoginResponseVO thirdPartyLogin(ThirdPartyLoginDTO thirdPartyLoginDTO);第三方登录以后再做

    //3.用户登出 - 核心：当前设备登出（传入JWT令牌，用于删除Redis对应记录）
    void logoutCurrentDevice(String jwtToken);

    //4.用户登出 - 扩展：所有设备登出（传入用户ID，批量删除Redis中该用户所有有效令牌）
    void logoutAllDevices(Long userId);

    //5.修改用户信息


}
