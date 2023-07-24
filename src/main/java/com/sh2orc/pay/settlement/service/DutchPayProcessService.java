package com.sh2orc.pay.settlement.service;

import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.DutchSettlementDetail;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import com.sh2orc.pay.settlement.enums.SettlementStatus;
import com.sh2orc.pay.settlement.exception.KkokioUserException;
import com.sh2orc.pay.settlement.exception.SettlementException;
import com.sh2orc.pay.settlement.repository.DutchSettlementDetailRepository;
import com.sh2orc.pay.settlement.repository.DutchSettlementRepository;
import com.sh2orc.pay.settlement.repository.KKokioUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Dutch pay process service.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DutchPayProcessService {

    private final KKokioUserRepository kKokioUserRepository;
    private final DutchSettlementRepository dutchSettlementRepository;
    private final DutchSettlementDetailRepository dutchSettlementDetailRepository;

    /**
     * 사용자는 다수의 사람들에게 금액을 지정하여 정산 요청
     *
     * @param owner            the owner
     * @param users            the users
     * @param settlementAmount the settlement amount
     */
    public DutchSettlement makeNewSettlement(KkokioUser owner, List<KkokioUser> users, Long settlementAmount){

        //더치페이 대표정산 엔티티 생성
        DutchSettlement dutchSettlement =
            DutchSettlement.of(owner, users.size() + 1, settlementAmount);

        dutchSettlementRepository.save(dutchSettlement);

        //1인당 더치페이 정산금액
        final Long divideAmount = dutchSettlement.getDivideAmount();

        // 더치페이 세부정산 내역 엔티티 생성
        List<DutchSettlementDetail> details = users.stream()
                   .map(user -> DutchSettlementDetail.of(dutchSettlement, user, divideAmount))
                   .collect(Collectors.toCollection(ArrayList::new));

        //정산 오너 세부내역 생성
        DutchSettlementDetail ownerSettlementDetail =
            DutchSettlementDetail.of(dutchSettlement, owner, divideAmount);

        //정산 완료 처리
        ownerSettlementDetail.complete();
        details.add(ownerSettlementDetail);

        //대표 정산 엔티티에 세부 정산 리스트 갱신
        dutchSettlement.setSettlementDetails(details);

        //세부내역 일괄 등록
        dutchSettlementDetailRepository.saveAll(details);

        return dutchSettlement;
    }

    /**
     * 요청받은 사용자는 정산하기 버튼을 통해서 요청한 요청자에게 금액을 송금
     *
     * @param dutchSettlementId the dutch settlement id
     * @param userId            the user id
     * @param amount            the amount
     */
    public DutchSettlementDetail processSettlementDetail(Long dutchSettlementId, Long userId, Long amount){

        //고객 계정 엔티티 조회
        KkokioUser user = kKokioUserRepository.findById(userId)
                                              .orElseThrow(KkokioUserException::notFound);

        //더치 정산 내역 조회
        DutchSettlement dutchSettlement = dutchSettlementRepository
                .findById(dutchSettlementId)
                .orElseThrow(SettlementException::notFound);

        //더치 세부정산 내역 조회
        DutchSettlementDetail dutchSettlementDetail = dutchSettlementDetailRepository
            .findByDutchSettlementAndKkokioUser(dutchSettlement, user)
            .orElseThrow(SettlementException::notFound);

        //정산 완료 여부 체크
        if(dutchSettlementDetail.getSettlementStatus() == SettlementStatus.COMPLETE){
            throw SettlementException.alreadyProcessed();
        }

        //남은 정산금액이 지급하려는 금액보다 작다면 amount 만큼만 정산
        Long settlementAmount = (dutchSettlementDetail.getUnpaidAmount() < amount)
            ? dutchSettlementDetail.getUnpaidAmount() : amount;

        //고객 계정 잔고 확인
        if( user.getBalance() < settlementAmount ){
            throw KkokioUserException.shortageBalance();
        }

        //정산 오너 대상자
        KkokioUser ownerUser = dutchSettlement.getOwnerUser();

        //고객 계정의 잔고를 줄이고, 정산 요청자의 잔고를 늘린다
        user.reduceBalance(settlementAmount);
        ownerUser.increaseBalance(settlementAmount);

        //정산 내역의 나머지 금액 (보통 unpaidAmount 만큼 차감, amount 작게 보낼 경우는 남음)
        Long paidAmount = dutchSettlementDetail.getUnpaidAmount() - settlementAmount;

        //나머지 금액이 더이상 없다면 정산 완료처리
        if(paidAmount == 0){

            dutchSettlementDetail.complete();

            List<DutchSettlementDetail> settlementDetails = dutchSettlement.getSettlementDetails();

            //세부 정산들이 모두 완료처리 되었는지
            boolean completed = settlementDetails.stream()
                    .allMatch(detail -> detail.getSettlementStatus() == SettlementStatus.COMPLETE);

            //더치페이 엔티티 상태 변경
            if(completed){
                dutchSettlement.complete();
            }else{
                dutchSettlement.ongoing();
            }
        } else {
            dutchSettlementDetail.update(paidAmount, SettlementStatus.ONGOING);
        }

        return dutchSettlementDetail;
    }

}
