package com.sh2orc.pay.settlement.service;

import com.sh2orc.pay.settlement.dto.DutchPayDto;
import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.DutchSettlementDetail;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Dutch pay api service.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DutchPayApiService {

    private final DutchPayProcessService dutchPayProcessService;
    private final DutchPayQueryService dutchPayQueryService;


    /**
     * 고객이 정산을 요청한 내역들 조회
     *
     * @param userId the user id
     * @return the list
     */
    public List<DutchPayDto.Settlement> getSettlementInfo(Long userId){

        // 더치페이 정산 조회 서비스
        List<DutchSettlement> dutchSettlement = dutchPayQueryService.getDutchSettlement(userId);

        return dutchSettlement.stream().map(settlement -> {

            //생성자 정보
            KkokioUser ownerUser = settlement.getOwnerUser();

            //DTO 생성 및 리턴
            return DutchPayDto.Settlement.builder()
                .id(settlement.getId())
                .ownerUserId(ownerUser.getId())
                .ownerName(ownerUser.getName())
                .ownerEmail(ownerUser.getEmail())
                .totalAmount(settlement.getSettlementAmount())
                .divideAmount(settlement.getSettlementAmount())
                .spareAmount(settlement.getDivideAmount())
                .status(settlement.getSettlementStatus())
                .build();

        }).toList();

    }

    /**
     * 고객이 정산을 요청 받은 내역들 조회
     *
     * @param userId the user id
     * @return the list
     */
    public List<DutchPayDto.SettlementDetail> getSettlementDetail(Long userId){

        //더치페이 세부 정산 조회
        List<DutchSettlementDetail> dutchDetails = dutchPayQueryService.getDutchDetails(userId);

        return dutchDetails.stream().map(detail ->{

            //N+1 문제 발생 구간
            DutchSettlement dutchSettlement = detail.getDutchSettlement();
            KkokioUser user = detail.getKkokioUser();

            return DutchPayDto.SettlementDetail.builder()
                .id(detail.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userMobileNo(user.getMobileNo())
                .userEmail(user.getEmail())
                .ownerName(dutchSettlement.getOwnerUser().getName())
                .status(detail.getSettlementStatus())
                .settlementAmount(detail.getSettlementAmount())
                .unpaidAmount(detail.getUnpaidAmount())
                .build();

        }).toList();
    }


}
