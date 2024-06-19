package com.zero.account.exception;

import com.zero.account.dto.ErrorResponse;
import com.zero.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} is occurred", e.getMessage());
        return new ErrorResponse(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleException(MethodArgumentNotValidException e) {
        log.error("{} is occurred", e.getMessage());
        return new ErrorResponse(ErrorCode.INVALID_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleException(DataIntegrityViolationException e) {
        log.error("{} is occurred", e.getMessage());
        return new ErrorResponse(ErrorCode.INVALID_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("{} is occurred", e.getMessage());
        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
