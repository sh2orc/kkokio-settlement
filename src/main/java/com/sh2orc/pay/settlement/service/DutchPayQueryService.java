package com.sh2orc.pay.settlement.service;

import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.DutchSettlementDetail;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import com.sh2orc.pay.settlement.exception.KkokioUserException;
import com.sh2orc.pay.settlement.exception.SettlementException;
import com.sh2orc.pay.settlement.repository.DutchSettlementDetailRepository;
import com.sh2orc.pay.settlement.repository.DutchSettlementRepository;
import com.sh2orc.pay.settlement.repository.KKokioUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DutchPayQueryService {

    private final KKokioUserRepository kKokioUserRepository;
    private final DutchSettlementRepository dutchSettlementRepository;
    private final DutchSettlementDetailRepository dutchSettlementDetailRepository;

    //요청자는 자신이 요청한 정산하기 전체 리스트의 정보 조회
    public List<DutchSettlement> getDutchSettlement(Long userId){

        //고객 계정 조회
        KkokioUser user = kKokioUserRepository
            .findById(userId)
            .orElseThrow(KkokioUserException::notFound);

        //정산 요청한 내역들에 대한 정보 조회
        List<DutchSettlement> dutchSettlements = dutchSettlementRepository
            .findByOwnerUser(user)
            .orElseThrow(SettlementException::notFound);

        //리스트 리턴
        return dutchSettlements;
    }

    //요청받은 사람은 자신이 요청 받은 정산하기 전체 리스트의 정보 조회
    public List<DutchSettlementDetail> getDutchDetails(Long userId){

        //고객 계정 조회
        KkokioUser user = kKokioUserRepository
            .findById(userId)
            .orElseThrow(KkokioUserException::notFound);

        //정산 세부 내역들 조회
        List<DutchSettlementDetail> details = dutchSettlementDetailRepository
            .findByKkokioUser(user)
            .orElseThrow(SettlementException::notFound);

        //리스트 리턴
        return details;
    }
}
