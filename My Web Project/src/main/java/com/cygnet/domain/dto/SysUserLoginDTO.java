package com.cygnet.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SysUserLoginDTO {

    // 校验失败会自动抛出 MethodArgumentNotValidException
    @NotBlank(message = "参数不能为空")
    private String usernameOrEmail;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    @NotNull
    private Boolean rememberMe;




}
