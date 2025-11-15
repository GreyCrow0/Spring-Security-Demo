package com.cygnet.controller;

import com.cygnet.domain.dto.SysUserLoginDTO;
import com.cygnet.domain.result.Result;
import com.cygnet.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("/login")
    public Result login(@RequestBody @Valid SysUserLoginDTO sysUserLoginDTO) {
        return sysUserService.login(sysUserLoginDTO);
    }

    @GetMapping("/logout")
    public Result logout() {
        return sysUserService.logout();
    }
}
