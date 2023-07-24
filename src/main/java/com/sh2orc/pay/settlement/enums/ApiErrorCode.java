package com.sh2orc.pay.settlement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode {

    OK(HttpStatus.OK, "0000", "정상"),
    ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND, null, "엔티티가 존재하지 않습니다."),
    ENTITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, null, "엔티티가 이미 존재합니다."),

    USER_NOT_EXISTS(HttpStatus.NOT_FOUND, null, "고객이 존재하지 않습니다."),
    USER_INVALID(HttpStatus.BAD_REQUEST, null, "유효한 고객이 아닙니다."),
    USER_BALANCE_SHORTAGE(HttpStatus.BAD_REQUEST, null, "고객의 잔고가 부족합니다"),

    SETTLEMENT_NOT_EXISTS(HttpStatus.NOT_FOUND, null, "정산이 존재하지 않습니다."),
    SETTLEMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, null, "이미 정산이 완료 되었습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
