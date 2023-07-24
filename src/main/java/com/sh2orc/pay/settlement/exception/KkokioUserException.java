package com.sh2orc.pay.settlement.exception;

import com.sh2orc.pay.settlement.enums.ApiErrorCode;

public class KkokioUserException extends ApiException{
    public KkokioUserException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    public static KkokioUserException notFound(){
        return new KkokioUserException(ApiErrorCode.ENTITY_NOT_EXISTS);
    }

    public static KkokioUserException shortageBalance() {
        return new KkokioUserException(ApiErrorCode.USER_BALANCE_SHORTAGE);
    }
}
