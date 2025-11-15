package com.cygnet.security;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cygnet.domain.constants.RedisConstants;
import com.cygnet.domain.constants.SecurityConstants;
import com.cygnet.domain.entity.SysUser;
import com.cygnet.domain.enums.ErrorEnum;

import com.cygnet.domain.enums.UserStatusEnum;
import com.cygnet.exception.IException;
import com.cygnet.util.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)  {
        try {
            if (isPublicUrl(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // jwt不存在
            if (ObjectUtil.isEmpty(token)) {
                log.warn("用户访问接口: {}, 提示:请先登录", request.getRequestURI());
                throw new IException(ErrorEnum.UNAUTHORIZED);
            }

            Long id = jwtUtils.parseToken(token);
            if (id == null) {
                log.warn("用户访问接口: {}, id不存在", request.getRequestURI());
                throw new IException(ErrorEnum.UNAUTHORIZED);
            }


            String userJson = stringRedisTemplate.opsForValue().get(RedisConstants.LoginTokenKey(id));

            if (StrUtil.isBlank(userJson)) {
                log.warn("用户访问接口: {}, 提示:用户不存在", request.getRequestURI());
                throw new IException(ErrorEnum.USERNAME_OR_PASSWORD_ERROR);
            }

            SysUser sysUser = JSONUtil.toBean(userJson, SysUser.class);

            if (sysUser == null) {
                log.error("JSON转换失败");
                throw new IException(ErrorEnum.SYSTEM_ERROR);
            }

            if (sysUser.getStatus() == UserStatusEnum.DISABLE) {
                log.warn("用户id: {}, 访问接口: {}, 提示:用户已被禁用", sysUser.getId(), request.getRequestURI());
                throw new IException(ErrorEnum.USER_DISABLED);
            }

            if (sysUser.getRole() == null) {
                log.warn("用户id: {}, 访问接口: {}, 提示:用户没有权限", sysUser.getId(), request.getRequestURI());
                throw new IException(ErrorEnum.AUTHORITY_LIMIT);
            }

            // 为角色添加 ROLE_ 前缀，这是Spring Security的约定
            String roleAuthority = "ROLE_" + sysUser.getRole().name();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    sysUser, null, Collections.singletonList(new SimpleGrantedAuthority(roleAuthority)));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 检查是否需要刷新token
            if (jwtUtils.shouldRefreshToken(token)) {
                String newAccessToken = jwtUtils.refreshToken(token);
                if (newAccessToken != null) {
                    // 在响应头中返回新的token
                    response.setHeader("X-New-Access-Token", newAccessToken);
                    log.debug("用户 {} 访问令牌已自动刷新", sysUser.getUsername());
                }
            }

            //放行
            log.info("用户id: {}, 权限: {}, 允许访问接口: {}", sysUser.getId(), sysUser.getRole().name(), request.getRequestURI());
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("解析用户token时发生异常，错误: {}", e.getMessage(), e);
            throw new IException(ErrorEnum.SYSTEM_ERROR);
        }
    }

    private boolean isPublicUrl(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // 登录接口特殊处理
        if ("GET".equalsIgnoreCase(method) && "/api/auth/login".equals(path)) {
            return true;
        }
        if ("POST".equalsIgnoreCase(method) && "/api/auth/register".equals(path)) {
            return true;
        }
        // 其他白名单路径
        for (String whitePath : SecurityConstants.PUBLIC_URLS) {
            if (path.equals(whitePath)) {
                return true;
            }
        }

        return false;
    }
}
