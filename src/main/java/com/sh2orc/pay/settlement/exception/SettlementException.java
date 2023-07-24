package com.sh2orc.pay.settlement.exception;

import com.sh2orc.pay.settlement.enums.ApiErrorCode;

public class SettlementException extends ApiException {

    public SettlementException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    public static SettlementException notFound(){
        return new SettlementException(ApiErrorCode.SETTLEMENT_NOT_EXISTS);
    }

    public static SettlementException alreadyProcessed(){
        return new SettlementException(ApiErrorCode.SETTLEMENT_ALREADY_COMPLETED);
    }
}
