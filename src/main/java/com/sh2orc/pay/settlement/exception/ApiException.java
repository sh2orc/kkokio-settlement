package com.sh2orc.pay.settlement.exception;

import com.sh2orc.pay.settlement.enums.ApiErrorCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{

    private final ApiErrorCode errorCode;
    private final String message;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public ApiException(ApiErrorCode errorCode, Object message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = String.valueOf(message);
    }

    public static ApiException of(ApiErrorCode errorCode){
        return new ApiException(errorCode);
    }

    public static ApiException entityNotFound(){
        return new ApiException(ApiErrorCode.ENTITY_NOT_EXISTS);
    }
    public static ApiException entityExists(){
        return new ApiException(ApiErrorCode.ENTITY_ALREADY_EXISTS);
    }
}
