package com.cygnet.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cygnet.domain.entity.LoginSysUser;
import com.cygnet.domain.entity.SysUser;
import com.cygnet.domain.enums.ErrorEnum;
import com.cygnet.domain.enums.UserStatusEnum;
import com.cygnet.exception.IException;
import com.cygnet.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import static com.cygnet.domain.enums.ErrorEnum.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (!StringUtils.hasText(usernameOrEmail)) {
            throw new UsernameNotFoundException(ErrorEnum.BAD_REQUEST.getDesc());
        }

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, usernameOrEmail)
                .or().eq(SysUser::getEmail, usernameOrEmail)
                .eq(SysUser::getIsDeleted, 0);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);

        if (ObjectUtils.isEmpty(sysUser)) {
            throw new UsernameNotFoundException(ErrorEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        }
        if (sysUser.getStatus().equals(UserStatusEnum.DISABLE)) {
            throw new DisabledException(ErrorEnum.USER_DISABLED.getDesc());
        }

        return new LoginSysUser(sysUser);
    }
}
