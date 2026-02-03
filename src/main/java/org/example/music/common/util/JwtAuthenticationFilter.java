package org.example.music.common.util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.music.common.exception.AuthException;
import org.example.music.dto.UserAuthDTO;
import org.example.music.service.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 注入授权Service
    private final AuthService authService;

    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 核心过滤方法：提取JWT -> 校验JWT -> 存入Security上下文
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 步骤1：从请求头中提取JWT令牌（遵循行业惯例：Authorization: Bearer {jwtToken}）
            String jwtToken = extractJwtFromRequest(request);

            // 步骤2：如果JWT不为空，且当前Security上下文无认证信息，执行校验
            if (StringUtils.hasText(jwtToken) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 调用AuthService校验JWT，获取用户信息
                UserAuthDTO userAuthDTO = authService.validateJwtToken(jwtToken);

                // 步骤3：构建Spring Security的认证令牌（无密码，因为JWT已校验通过）
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userAuthDTO, // 主体：存入用户认证信息DTO
                        null, // 凭证：JWT授权无需密码，填null
                        null  // 权限列表：如果需要权限控制，可在这里传入用户角色/权限，暂时填null
                );

                // 步骤4：设置请求详情（可选，增强安全性）
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 步骤5：将认证令牌存入Security上下文，后续接口可直接获取用户信息
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            // 步骤6：放行请求，继续执行后续过滤器链
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            // 步骤7：授权异常处理，返回401未授权响应（JSON格式，方便前端处理）
            handleAuthException(response, e);
        }
    }

    /**
     * 从请求头中提取JWT令牌
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        // 校验格式：是否以"Bearer "开头
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // 截取"Bearer "后面的JWT令牌
        }
        return null;
    }

    /**
     * 处理授权异常，返回统一的JSON响应
     */
    private void handleAuthException(HttpServletResponse response, AuthException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
        response.setContentType("application/json;charset=UTF-8");
        // 封装统一的错误返回格式（可根据项目需求调整）
        String errorJson = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", e.getMessage());
        response.getWriter().write(errorJson);
    }
}