package com.cygnet.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegisterTypeEnum {

    EMAIL(0),
    USERNAME(1);

    //告诉MyBatis实际存这个值
    @EnumValue
    private final Integer code;

}
