package org.example.music.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson2.JSON;

/**
 * JWT工具类（适配 jjwt 0.11.5 版本，无弃用警告）
 */
@SuppressWarnings("SpellCheckingInspection")
@Component
public class JwtUtil {

    /**
     * JWT 密钥（从配置文件读取，生产环境务必设置复杂密钥）jwt.secret=your-secret-key-1234567890abcdefghijklmnopqrstuvwxyz
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT 默认过期时间86400000
     */
    @Value("${jwt.expiration}")
    private long defaultExpireSeconds;

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 生成安全的密钥（HS256 算法要求密钥长度至少 256 位）
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 生成JWT令牌（支持自定义过期时间）
     */
    public String generateToken(Long userId, String username, long expireSeconds) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireSeconds * 1000))
                // 0.11.5 版本的正确签名方式
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT令牌（使用默认过期时间）
     */
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, defaultExpireSeconds);
    }

    /**
     * 解析JWT令牌，获取载荷信息
     */
    public Claims getClaimsByToken(String token) {
        try {
            return Jwts.parserBuilder()
                    // 0.11.5 版本的正确验签方式
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT令牌已过期");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("不支持的JWT令牌格式");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("JWT令牌格式错误，无效令牌");
        } catch (SignatureException e) {
            throw new RuntimeException("JWT签名验证失败，令牌被篡改");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT令牌为空或格式非法");
        }
    }

    /**
     * 从令牌中提取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中提取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 验证令牌是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsByToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private final RedisTemplate<String, String> redisTemplate;
    // ---------------------- 新增：Redis相关常量和工具方法 ----------------------
    /**
     * Redis Key 前缀：有效JWT令牌（白名单）
     * 格式：auth:valid_token:用户ID:令牌MD5摘要
     */
    public static final String REDIS_KEY_PREFIX_VALID_JWT = "auth:valid_token:";

    /**
     * 生成JWT的MD5摘要（用于拼接Redis Key，缩短长度）
     */
    public String getJwtMd5Digest(String jwtToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestBytes = md.digest(jwtToken.getBytes());
            // 转成16进制字符串（32位，长度固定，便于存储）
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // 异常时直接返回JWT本身（兜底，避免流程中断）
            return jwtToken;
        }
    }

    /**
     * 拼接Redis完整Key
     * @param userId 用户ID
     * @param jwtToken JWT令牌
     * @return 完整的Redis Key
     */
    public String buildRedisJwtKey(Long userId, String jwtToken) {
        // 拼接格式：REDIS_KEY_PREFIX_VALID_JWT + userId + ":" + jwtMd5Digest
        return REDIS_KEY_PREFIX_VALID_JWT + userId + ":" + getJwtMd5Digest(jwtToken);
    }

    // ---------------------- 核心方法：将JWT存入Redis白名单 ----------------------
    /**
     * 把生成的JWT存入Redis白名单（与JWT过期时间一致）
     * @param userId 用户ID
     * @param username 用户名
     * @param jwtToken 生成的JWT令牌
     * @param expireSeconds JWT过期时间（秒）
     */
    public void saveJwtToRedisWhiteList(Long userId, String username, String jwtToken, long expireSeconds) {
        // 1. 拼接唯一的Redis Key
        String redisKey = buildRedisJwtKey(userId, jwtToken);

        // 2. 构造要存储的Value（用户核心信息，便于后续排查）
        Map<String, Object> jwtValue = new HashMap<>(4);
        jwtValue.put("userId", userId);
        jwtValue.put("username", username);
        jwtValue.put("jwtDigest", getJwtMd5Digest(jwtToken));
        jwtValue.put("expireTime", System.currentTimeMillis() + expireSeconds * 1000); // 过期时间戳（毫秒）
        String redisValue = JSON.toJSONString(jwtValue);

        // 3. 存入Redis，并设置过期时间（与JWT过期时间一致，单位：秒）
        redisTemplate.opsForValue().set(redisKey, redisValue, expireSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }
}