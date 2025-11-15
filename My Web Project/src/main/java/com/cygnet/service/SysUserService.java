package com.cygnet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cygnet.domain.dto.SysUserLoginDTO;
import com.cygnet.domain.entity.SysUser;
import com.cygnet.domain.result.Result;

public interface SysUserService extends IService<SysUser> {

    Result login(SysUserLoginDTO sysUserLoginDTO);

    Result logout();
}
