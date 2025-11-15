package com.cygnet.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusEnum {

    NORMAL(0),
    DISABLE(1);

    //告诉MyBatis实际存这个值
    @EnumValue
    private final Integer code;


}
