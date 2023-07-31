package com.sh2orc.pay.settlement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sh2orc.pay.settlement.enums.ApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Boolean success;
    private String code;
    private String message;
    private T data;
    private ApiErrorCode apiErrorCode;

    public static <T> ApiResponse success(T data){
        return ApiResponse.builder()
            .success(true)
            .code(ApiErrorCode.OK.getCode())
            .data(data)
            .apiErrorCode(ApiErrorCode.OK)
            .build();
    }

    public static <T> ApiResponse success(T data, String message){
        return ApiResponse.builder()
            .success(true)
            .code(ApiErrorCode.OK.getCode())
            .message(message)
            .data(data)
            .apiErrorCode(ApiErrorCode.OK)
            .build();
    }


    public static <T> ApiResponse failure(ApiErrorCode errorCode){
        return ApiResponse.builder()
            .success(false)
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .apiErrorCode(errorCode)
            .build();
    }

    public static <T> ApiResponse failure(T data, ApiErrorCode errorCode){
        return ApiResponse.builder()
            .success(false)
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .data(data)
            .apiErrorCode(errorCode)
            .build();
    }


}
