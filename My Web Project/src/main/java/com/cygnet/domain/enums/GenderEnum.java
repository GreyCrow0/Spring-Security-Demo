package com.cygnet.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GenderEnum {

    MALE(0, "男"),
    FEMALE(1, "女");

    //告诉MyBatis实际存这个值
    @EnumValue
    private final Integer code;

    private final String desc;

}
