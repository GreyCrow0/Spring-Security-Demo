package com.cygnet.exception;

import com.cygnet.domain.enums.ErrorEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 自定义异常类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Getter
public class IException extends RuntimeException{

    private final ErrorEnum errorEnum;

    // 自定义异常
    public IException(ErrorEnum e){
        errorEnum = e;
    }
}