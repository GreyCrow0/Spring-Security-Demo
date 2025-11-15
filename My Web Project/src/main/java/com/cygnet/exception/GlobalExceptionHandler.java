package com.cygnet.exception;

import com.cygnet.domain.enums.ErrorEnum;
import com.cygnet.domain.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IException.class)
    public Result<ErrorEnum> handleException(IException e) {
        log.error("业务异常 {}", e.getErrorEnum().getDesc());
        return Result.error(e.getErrorEnum());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<ErrorEnum> handleUsernameNotFound(UsernameNotFoundException e) {
        log.error("业务异常 {}", ErrorEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        return Result.error(ErrorEnum.USERNAME_OR_PASSWORD_ERROR);
    }

    @ExceptionHandler(DisabledException.class)
    public Result<ErrorEnum> handleUserDisabled(DisabledException e) {
        log.error("业务异常 {}", ErrorEnum.USER_DISABLED.getDesc());
        return Result.error(ErrorEnum.USER_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Result<ErrorEnum> handleBadCredentials(BadCredentialsException e) {
        log.error("业务异常 {}", ErrorEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        return Result.error(ErrorEnum.USERNAME_OR_PASSWORD_ERROR);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public Result<ErrorEnum> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error("业务异常 {}", ErrorEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        return Result.error(ErrorEnum.USERNAME_OR_PASSWORD_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<ErrorEnum> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("业务异常 {}", ErrorEnum.BAD_REQUEST.getDesc());
        return Result.error(ErrorEnum.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public Result<ErrorEnum> handleException(Exception e) {
        log.error("系统异常 {} ", e.getMessage(), e);
        return Result.error(ErrorEnum.SYSTEM_ERROR);
    }

}
