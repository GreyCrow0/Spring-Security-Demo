package com.cygnet.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {

    ADMIN(0, "管理员"),
    USER(1, "普通用户"),
    VIP(2, "vip");

    //告诉MyBatis实际存这个值
    @EnumValue
    private final Integer code;

    private final String desc;

}
