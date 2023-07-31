package com.sh2orc.pay.settlement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sh2orc.pay.settlement.enums.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class DutchPayDto {

    /**
     * [Request] 더치페이 정산 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestSettlementProcess{
        private Long userId;            // 요청한 유저 ID
        private List<Long> targetIds;   // 정산 대상 유저 ID 리스트
        private Long amount;            // 정산 금액
    }


    /**
     * [Request] 더치페이 세부 정산 처리 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDetailProcess{
        private Long userId;
        private Long settlementId;
        private Long amount;
    }


    /**
     * [Response] 더치페이 정산 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Settlement{
        private Long id;                    // N정산 id
        private Long ownerUserId;           // 정산 오너 유저ID
        private String ownerName;           // 정산 오너 이름
        private String ownerEmail;          // 정산 오너 이메일
        private Long totalAmount;           // 정산 전체 금액
        private Long divideAmount;          // 정산 1/N 금액
        private Long spareAmount;           // 나머지 금액 (카카오 부담)
        private SettlementStatus status;    // 정산 상태

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime requestDateTime;  //요청일자
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime completeDateTime; //완료일자

        private List<SettlementDetail> settlementDetails;   //유저 정산 리스트
    }

    /**
     * [Response] 더치페이 세부 정산 내역
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SettlementDetail{
        private Long id;                        //유저 정산 엔티티 ID
        private Long userId;                    //정산 대상 유저 ID
        private String userName;                //정산 대상 이름
        private String userMobileNo;            //정산 대상 휴대폰번호
        private String userEmail;               //정산 대상 이메일
        private String ownerName;               //정산에 대한 오너(정산 요청자)
        private SettlementStatus status;        //유저 정산 상태
        private Long totalAmount;               //전체 정산금액 (얼마짜리를 정산하는 것인지)
        private Long settlementAmount;          //유저에게 할당된 정산금액
        private Long unpaidAmount;              //유저가 지급하지 않은 정산금액

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime requestDateTime;  //생성일자
    }


}

