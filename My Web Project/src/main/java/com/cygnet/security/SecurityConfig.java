package com.cygnet.security;

import com.cygnet.domain.constants.SecurityConstants;
import com.cygnet.domain.enums.ErrorEnum;
import com.cygnet.util.WebUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Resource
    private UserDetailsServiceImpl userDetailService;

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 设置认证管理器
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").anonymous()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint())      // 认证失败处理
                .accessDeniedHandler(accessDeniedHandler())               // 权限不足处理
        );
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        // 配置跨域
        http.cors(cors -> cors.configurationSource(configurationSource()));
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.warn("用户 {} 权限不足，访问: {} {}",
                        authentication.getName(), request.getMethod(), request.getRequestURI());
            }
            WebUtils.returnFrontendResponse(response, ErrorEnum.AUTHORITY_LIMIT);
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authenticationException) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.warn("用户 {} 未登录，访问: {} {}",
                        authentication.getName(), request.getMethod(), request.getRequestURI());
            }
            WebUtils.returnFrontendResponse(response, ErrorEnum.UNAUTHORIZED);
        };
    }


//    // 创建AuthenticationManager
//    @Bean
//    public AuthenticationManager sysUserAuthenticationManager() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailService);
//        return new ProviderManager(provider);
//    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        // 让Spring Boot自动创建，它会自动使用我们定义的UserDetailsService和PasswordEncoder
        return config.getAuthenticationManager();
    }

    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置跨域，允许跨域 配置CorsConfigurationSource
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "https://localhost:8888",
                "https://localhost:3000"
        )); // 只

        // 创建 CorsConfigurationSource对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
