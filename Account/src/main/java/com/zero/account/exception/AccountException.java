package com.zero.account.exception;

import com.zero.account.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    public AccountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }
}
