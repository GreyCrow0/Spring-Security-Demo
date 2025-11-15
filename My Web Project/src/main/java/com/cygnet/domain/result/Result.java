package com.cygnet.domain.result;

import com.cygnet.domain.enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.cygnet.domain.enums.ErrorEnum.SUCCESS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(SUCCESS.getCode(), SUCCESS.getDesc(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS.getCode(), SUCCESS.getDesc(), data);
    }

    public static <T> Result<T> error(ErrorEnum errorEnum) {
        return new Result<>(errorEnum.getCode(), errorEnum.getDesc(), null);
    }
}