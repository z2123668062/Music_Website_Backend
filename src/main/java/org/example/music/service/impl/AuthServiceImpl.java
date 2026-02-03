package org.example.music.service.impl;

import com.alibaba.fastjson2.JSON;
import org.example.music.common.exception.AuthException;
import org.example.music.common.util.JwtUtil;
import org.example.music.dto.UserAuthDTO;
import org.example.music.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    // 注入Jwt工具类
    private final JwtUtil jwtUtil;

    // 注入RedisTemplate
    private final RedisTemplate<String, String> redisTemplate;

    public AuthServiceImpl(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 核心校验方法：校验JWT有效性 + 校验Redis白名单
     * @param jwtToken 前端传递的JWT令牌
     * @return 封装后的用户认证信息
     * @throws AuthException 授权异常（令牌无效、已登出等）
     */
    public UserAuthDTO validateJwtToken(String jwtToken) {
        // 步骤1：判空校验（前置兜底）
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthException("JWT令牌不能为空");
        }

        // 步骤2：JWT基础有效性校验（格式、签名、是否过期）
        if (!jwtUtil.isTokenValid(jwtToken)) {
            throw new AuthException("JWT令牌无效或已过期");
        }

        // 步骤3：提取JWT中的用户信息
        Long userId = jwtUtil.getUserIdFromToken(jwtToken);
        String username = jwtUtil.getUsernameFromToken(jwtToken);
        if (userId == null || username == null) {
            throw new AuthException("JWT令牌中用户信息不完整");
        }

        // 步骤4：拼接Redis Key（与JwtUtil中存储的格式完全一致，保证能查询到）
        String redisKey = JwtUtil.REDIS_KEY_PREFIX_VALID_JWT + userId + ":" + jwtUtil.getJwtMd5Digest(jwtToken);

        // 步骤5：Redis白名单校验（查询该令牌是否仍在有效白名单中）
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null || redisValue.trim().isEmpty()) {
            throw new AuthException("JWT令牌已失效（已登出）");
        }

        // 步骤6：校验通过，封装用户信息并返回（可额外解析Redis中的信息做兜底校验，可选）
        Map<String, Object> userInfoMap = JSON.parseObject(redisValue);
        if (!userId.equals(userInfoMap.get("userId")) || !username.equals(userInfoMap.get("username"))) {
            throw new AuthException("JWT令牌与Redis中存储的用户信息不一致");
        }

        return new UserAuthDTO(userId, username);
    }
}
