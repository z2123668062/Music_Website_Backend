package org.example.music.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.music.common.util.JwtUtil;
import org.example.music.dto.LocalLoginDTO;
import org.example.music.dto.LocalRegisterDTO;
import org.example.music.entity.User;
import org.example.music.mapper.UserMapper;
import org.example.music.service.UserService;
import org.example.music.vo.LoginResponseVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder; // 明确指定为BCryptPasswordEncoder
    private final StringRedisTemplate stringRedisTemplate; // 注入Redis操作模板
    private final JwtUtil jwtUtil;

    // Redis相关常量（与你定义的保持一致，复用你的配置）
    private static final String REDIS_CODE_KEY_PREFIX = "verify:code:";
    private static final long CODE_EXPIRE_SECONDS = 300;
    private static final int CODE_LENGTH = 6;


    // 邮箱和手机号正则（补充业务校验使用）
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    // 6. 登录类型常量（避免硬编码，方便维护）
    private static final String LOGIN_TYPE_PASSWORD = "password"; // 密码登录
    private static final String LOGIN_TYPE_VERIFY = "verify"; //验证码登录
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void localRegister(LocalRegisterDTO userRegisterDTO){
        log.info("开始处理用户注册，用户名: {}", userRegisterDTO.getUsername());
        try{
            // 1. 基本参数校验（Controller已通过@Valid校验，这里做业务补充校验）
            String verifyType = userRegisterDTO.getVerifyType();
            String verifyAccount = userRegisterDTO.getVerifyAccount();
            // 校验验证方式只能是 email 或 phone
            if (!"email".equals(verifyType) && !"phone".equals(verifyType)) {
                throw new RuntimeException("验证方式不合法，仅支持 email 或 phone");
            }
            //校验验证账号与验证方式匹配（邮箱格式/手机号格式）
            if("email".equals(verifyType)&&!EMAIL_PATTERN.matcher(verifyAccount).matches()){
                throw new RuntimeException("邮箱格式不合法");
            }
            if("phone".equals(verifyType)&&!PHONE_PATTERN.matcher(verifyAccount).matches()){
                throw new RuntimeException("手机号格式不合法（11位有效手机号）");
            }

            //2.检查用户名是否已存在
            User existUser = userMapper.selectUserByUsername(userRegisterDTO.getUsername());
            if(existUser != null){
                throw new RuntimeException("该用户名已被其他用户注册");
            }

            //3.检查邮箱/手机号是否存在
            User existingUserByVerifyAccount;
            if("email".equals(verifyType)){
                existingUserByVerifyAccount=userMapper.selectUserByEmail(userRegisterDTO.getVerifyAccount());
                if(existingUserByVerifyAccount!=null){
                    throw new RuntimeException("该邮箱已被注册，请更换其他邮箱或直接登录");
                }
            }else {
                existingUserByVerifyAccount=userMapper.selectUserByMobile(userRegisterDTO.getVerifyAccount());
                if (existingUserByVerifyAccount != null) {
                    throw new RuntimeException("该手机号已被注册，请更换其他手机号或直接登录");
                }
            }
            //4.验证验证码是否正确
            // 4.1 拼接Redis key（与你存储验证码的格式一致）
            String redisKey = REDIS_CODE_KEY_PREFIX + verifyType + ":" + verifyAccount;
            // 4.2 从Redis获取存储的验证码
            String redisStoredCode = stringRedisTemplate.opsForValue().get(redisKey);
            // 4.3 验证码校验（先判断是否存在，再判断是否匹配）
            if (redisStoredCode == null) {
                throw new RuntimeException("验证码已过期或未获取，请重新获取验证码");
            }
            if (!redisStoredCode.equals(userRegisterDTO.getVerifyCode())) {
                throw new RuntimeException("验证码输入错误，请核对后重新输入");
            }
            // 可选：验证码验证通过后，立即删除Redis中的验证码（防止重复使用）
            stringRedisTemplate.delete(redisKey);

            // 5. 创建用户实体并保存到数据库（使用BCryptPasswordEncoder加密密码）
            User user = new User();
            user.setUsername(userRegisterDTO.getUsername());
            // 核心：使用BCryptPasswordEncoder对原始密码进行加密存储（不可逆加密）
            user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
            // 赋值邮箱/手机号（根据验证类型）
            if ("email".equals(verifyType)) {
                user.setEmail(verifyAccount);
            } else {
                user.setMobile(verifyAccount);
            }
            // 账号状态默认 0（正常）
            user.setStatus(0);

            // 保存用户到数据库（MyBatis-Plus 插入方法，主键id自动回显）
            int insertResult = userMapper.insert(user);
            if (insertResult <= 0) {
                throw new RuntimeException("用户信息保存失败，请重试");
            }

         }catch (Exception e) {
            log.error("用户注册失败，用户名: {}，错误: {}", userRegisterDTO.getUsername(), e.getMessage());
            throw e; // 抛出异常让全局异常处理器处理
        }
    }

    @Override
    public LoginResponseVO localLogin(LocalLoginDTO localLoginDTO){
        //1.提取DTO中的参数
        String account= localLoginDTO.getAccount();
        String loginType = localLoginDTO.getLoginType();
        String password = localLoginDTO.getPassword();
        String verifyType = localLoginDTO.getVerifyType();
        String verifyCode = localLoginDTO.getVerifyCode();
        Boolean rememberMe = localLoginDTO.getRememberMe();

        //2.根据account查询用户是否存在
        User user = userMapper.selectUserByAccount(account);
        if(user == null){
            throw new RuntimeException("用户不存在，请检查登录账号是否正确，或重新注册");
        }

        //3.区分登录方式进行校验
        //3.1密码登录
        if(LOGIN_TYPE_PASSWORD.equals(loginType)){
            //非空校验
            if(password==null||password.trim().isEmpty()){
                throw new RuntimeException("密码不能为空");
            }
            //用BCryptPasswordEncoder比对明文密码和数据库加密密码
            boolean passwordMatch=bCryptPasswordEncoder.matches(password,user.getPassword());
            if(!passwordMatch){
                throw new RuntimeException("密码错误，请重新输入");
            }
        }

        //3.2验证码登录
        else if(LOGIN_TYPE_VERIFY.equals(loginType)){
            //非空校验
            if(verifyType==null||verifyType.trim().isEmpty()){
                throw new RuntimeException("验证方式不能为空：email/phone");
            }
            if(verifyCode==null||verifyCode.trim().isEmpty()){
                throw new RuntimeException("验证码不能为空");
            }
            //拼接Redis中的验证码key
            String redisKey = REDIS_CODE_KEY_PREFIX + verifyType + ":" + account;
            //从Redis里面取验证码
            String redisStoredCode = stringRedisTemplate.opsForValue().get(redisKey);
            //校验验证码
            if (redisStoredCode == null) {
                throw new RuntimeException("验证码已过期或未发送，请重新获取");
            }
            if(!verifyCode.equals(redisStoredCode)){
                throw new RuntimeException("验证码错误，请重新输入");
            }
            //通过之后删除Redis里面的验证码
            stringRedisTemplate.delete(redisKey);
        }
        else{
            throw new RuntimeException("不支持的登录方式，请选择密码登录或验证码重新登录");
        }
        //校验通过，生成JWT令牌
        long expireTime;
        if(Boolean.TRUE.equals(rememberMe)){
            expireTime = 60*60*24*7;
        }else{
            expireTime=2*60*60;
        }

        //生成JWT令牌
        String jwtToken= jwtUtil.generateToken(user.getId(), user.getUsername(),expireTime);
        //将JWT令牌存入Redis白名单
        jwtUtil.saveJwtToRedisWhiteList(user.getId(), user.getUsername(), jwtToken, expireTime);
        //组装LoginResponseVO并返回
        LoginResponseVO loginResponseVO = new LoginResponseVO();
        loginResponseVO.setUserId(user.getId());
        loginResponseVO.setUsername(user.getUsername());
        loginResponseVO.setAvatar(user.getAvatar());
        loginResponseVO.setToken(jwtToken);
        loginResponseVO.setMessage("登录成功");

        return loginResponseVO;
    }
    //第三方登录先不做

    //用户登出
    @SuppressWarnings("PointlessBooleanExpression")
    @Override
    public void logoutCurrentDevice(String jwtToken) {
        log.info("开始处理用户当前设备登出请求");
        try{
            //1.JWT令牌非空校验
            if(jwtToken==null||jwtToken.trim().isEmpty()){
                throw new RuntimeException("JWT令牌不能为空，无法完成登出");
            }

            //2.解析JWT中的用户ID（仅校验格式和签名，不严格校验过期）
            Long userId;
            try{
                userId = jwtUtil.getUserIdFromToken(jwtToken);
            }catch (Exception e){
                log.error("JWT令牌无效，无法解析用户ID，错误信息:{}",e.getMessage());
                throw new RuntimeException("无效的JWT令牌，无法完成登出");
            }
            if(userId==null){
                throw new RuntimeException("JWT令牌中用户信息不完整，无法完成登出");
            }

            //3.拼接Redis Key（与登录时存储的格式完全一致，复用JwtUtil中的方法和常量）
            String redisKey = jwtUtil.buildRedisJwtKey(userId, jwtToken);

            //4.删除Redis中对应的JWT记录
            Boolean isDeleted =stringRedisTemplate.delete(redisKey);
            if(Boolean.FALSE.equals(isDeleted)){
                log.warn("Redis中未查询到该JWT记录，令牌已失效，用户ID：{}",userId);
                throw new RuntimeException("令牌已失效，无需重复登出");
            }

            log.info("用户当前设备登出成功，用户ID：{}",userId);
        }catch (Exception e){
            log.error("用户当前设备登出失败，错误信息：{}",e.getMessage());
            throw e;
        }
    }
    @Override
    public void logoutAllDevices(Long userId) {
        log.info("开始处理用户所有设备登出请求，用户ID：{}", userId);
        try {
            // 步骤1：用户ID判空校验
            if (userId == null) {
                throw new RuntimeException("用户ID不能为空，无法完成所有设备登出");
            }

            // 步骤2：拼接Redis Key模糊匹配前缀（与JwtUtil中存储格式一致：auth:valid_token:用户ID:*）
            String redisKeyPattern = JwtUtil.REDIS_KEY_PREFIX_VALID_JWT + userId + ":*";

            // 步骤3：模糊查询该用户所有有效JWT记录并批量删除
            stringRedisTemplate.keys(redisKeyPattern).forEach(redisKey -> {
                stringRedisTemplate.delete(redisKey);
                log.debug("删除用户有效JWT记录，Redis Key：{}", redisKey);
            });

            log.info("用户所有设备登出成功，用户ID：{}", userId);
        } catch (Exception e) {
            log.error("用户所有设备登出失败，用户ID：{}，错误信息：{}", userId, e.getMessage());
            throw e; // 抛出异常让全局异常处理器统一处理
        }
    }

}
