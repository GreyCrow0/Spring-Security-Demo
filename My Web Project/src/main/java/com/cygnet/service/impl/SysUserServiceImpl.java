package com.cygnet.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cygnet.domain.constants.RedisConstants;
import com.cygnet.domain.dto.SysUserLoginDTO;
import com.cygnet.domain.entity.LoginSysUser;
import com.cygnet.domain.entity.SysUser;
import com.cygnet.domain.result.Result;
import com.cygnet.mapper.SysUserMapper;
import com.cygnet.service.SysUserService;
import com.cygnet.util.JwtUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public Result login(SysUserLoginDTO sysUserLoginDTO) {

        try {

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sysUserLoginDTO.getUsernameOrEmail(), sysUserLoginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            //3.在Authentication中获取用户信息
            SysUser sysUser = ((LoginSysUser) authentication.getPrincipal()).getSysUser();

            String token = jwtUtils.createToken(sysUser.getId(), sysUserLoginDTO.getRememberMe());

            //信息存入redis
            stringRedisTemplate.opsForValue().set(RedisConstants.LoginTokenKey(sysUser.getId()), JSONUtil.toJsonStr(sysUser), RedisConstants.USER_TOKEN_TIMEOUT);

            log.info("用户{}登录成功, 角色 - {}", sysUser.getUsername(), sysUser.getRole().name());

            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return Result.success(map);

        } catch (Exception e) {
            log.error("用户{} 登录失败, 异常: {}",  sysUserLoginDTO.getUsernameOrEmail(), e.getMessage());
            throw e; //让全局异常处理器处理
        }
    }

    @Override
    public Result logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser sysUser = (SysUser) authentication.getPrincipal();
        stringRedisTemplate.delete(RedisConstants.LoginTokenKey(sysUser.getId()));
        log.info("用户:{} - id: {} - 权限; {} 退出登录", sysUser.getUsername(), sysUser.getId(), sysUser.getRole().name());
        return  Result.success();
    }
}
