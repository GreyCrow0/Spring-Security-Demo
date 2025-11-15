package com.cygnet.domain.entity;

import com.cygnet.domain.enums.UserStatusEnum;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LoginSysUser implements UserDetails {

    private final SysUser sysUser;

    /**
     * 权限列表（基于角色生成）
     */
    private List<String> permissions;

    private List<GrantedAuthority> authorities;

    public LoginSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        // 如果没有设置权限列表，则基于角色生成基础权限
        if (permissions == null || permissions.isEmpty()) {
            authorities = generateAuthoritiesFromRole();
        } else {
            // 基于权限字符串生成权限对象
            authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return authorities;
    }

    private List<GrantedAuthority> generateAuthoritiesFromRole() {
        if (sysUser.getRole() == null) {
            return Collections.emptyList();
        }

        // 为角色添加 ROLE_ 前缀，这是Spring Security的约定
        String roleAuthority = "ROLE_" + sysUser.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleAuthority));

    }

    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return sysUser.getStatus().equals(UserStatusEnum.NORMAL);
    }
}
