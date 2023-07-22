package com.sh2orc.pay.settlement.service;

import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.DutchSettlementDetail;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import com.sh2orc.pay.settlement.enums.SettlementStatus;
import com.sh2orc.pay.settlement.repository.DutchSettlementDetailRepository;
import com.sh2orc.pay.settlement.repository.DutchSettlementRepository;
import com.sh2orc.pay.settlement.repository.KKokioUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DutchPayProcessServiceTest {

    /********************************************
     * Service
     *******************************************/
    @Autowired
    private DutchPayProcessService dutchPayProcessService;


    /********************************************
     * Repository
     *******************************************/
    @Autowired //유저 계정
    private KKokioUserRepository userRepository;
    @Autowired
    private DutchSettlementRepository dutchSettlementRepository;
    @Autowired
    private DutchSettlementDetailRepository dutchSettlementDetailRepository;

    List<KkokioUser> participants;

    @BeforeEach
    @Transactional
    public void setUp(){

        KkokioUser 홍길동 = KkokioUser.builder()
                                   .email("xyz@kkoiko.com")
                                   .mobileNo("01088881234")
                                   .name("홍길동")
                                   .balance(50_000L)
                                   .build();

        KkokioUser 김이박 = KkokioUser.builder()
                                   .email("abc@kkomuruk.com")
                                   .mobileNo("01088889876")
                                   .name("김이박")
                                   .balance(50_000L)
                                   .build();

        KkokioUser 주나라 = KkokioUser.builder()
                                   .email("help@kkokko.com")
                                   .mobileNo("01012349999")
                                   .name("주나라")
                                   .balance(50_000L)
                                   .build();

        participants = List.of(홍길동, 김이박, 주나라);
        userRepository.saveAll(participants);
    }

    @Test
    @DisplayName("정산을 생성하는 테스트 ")
    @Transactional
    void makeNewSettlement() {

        //given
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);

        //when
        DutchSettlement dutchSettlement =
            dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);

        //then
        //세부 정산 내역 조회
        List<DutchSettlementDetail> settlementDetails = dutchSettlementDetailRepository
                .findByDutchSettlement(dutchSettlement)
                .orElseThrow(() -> new RuntimeException("세부정산 데이터가 없음"));

        //세부정산 내역 검증
        Assertions.assertEquals(3, settlementDetails.size());

        //상태별 엔티티 건수 검증
        Long readyCount = settlementDetails.stream()
                 .filter(settle -> settle.getSettlementStatus() == SettlementStatus.READY)
                 .count();

        Long completeCount = settlementDetails.stream()
                 .filter(settle -> settle.getSettlementStatus() == SettlementStatus.COMPLETE)
                 .count();

        Assertions.assertEquals(2, readyCount);
        Assertions.assertEquals(1, completeCount);

        //더치정산 대상자수
        Assertions.assertEquals( 3, dutchSettlement.getDutchPeopleCount());
        //정산금액
        Assertions.assertEquals(50000L, dutchSettlement.getSettlementAmount());
    }

    @Test
    @DisplayName("요청 받은 사용자의 정산 테스트 ")
    @Transactional
    void processSettlementDetail() {

        /**------------------------------
         * given : 필요 데이터 설정 및 초기화
         *-------------------------------*/
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);

        DutchSettlement dutchSettlement =
            dutchPayProcessService.makeNewSettlement(owner,
                                                     List.of(target1, target2),
                                                     50000L);

        Long ducthId = dutchSettlement.getId();

        /**-----------------------------------
         * when : 더치 페이 처리 메서드 호출
         *------------------------------------*/
        //정산대상1 100만원으로 정산 처리
        DutchSettlementDetail target1Detail =
            dutchPayProcessService.processSettlementDetail(ducthId, target1.getId(), 1_000_000L);
        //정산대상2 1000원만 정산 처리
        DutchSettlementDetail target2Detail =
            dutchPayProcessService.processSettlementDetail(ducthId, target2.getId(), 1000L);

        /**-----------------------------------
         * then : 더치페이 처리 결과에 대한 검증
         *------------------------------------*/
        //타겟1에 대한 엔티티 비교
        Assertions.assertEquals(0L, target1Detail.getUnpaidAmount());
        Assertions.assertEquals(SettlementStatus.COMPLETE, target1Detail.getSettlementStatus());

        //타겟2에 대한 엔티티 비교
        Long divideAmount = 50_000L / 3;
        Assertions.assertEquals(divideAmount - 1000L, target2Detail.getUnpaidAmount());
        Assertions.assertEquals(SettlementStatus.ONGOING, target2Detail.getSettlementStatus());

        //잔고 확인
        Assertions.assertEquals(50_000L - 16_666L, target1.getBalance());
        Assertions.assertEquals(50_000L - 1_000L, target2.getBalance());
        Assertions.assertEquals(50_000L + 16_666L + 1_000L, owner.getBalance());


        /**-----------------------------------
         * when : 더치 페이 나머지 금액에 대해서도 정산 처리
         *------------------------------------*/
        //정산대상2 15,666원 정산 처리
        dutchPayProcessService.processSettlementDetail(ducthId, target2.getId(), 15_666L);

        /**-----------------------------------
         * then : 처리 결과에 대한 검증
         *------------------------------------*/
        //타겟2에 대한 엔티티 비교
        Assertions.assertEquals(0L, target2Detail.getUnpaidAmount());
        Assertions.assertEquals(SettlementStatus.COMPLETE, target2Detail.getSettlementStatus());

        //세부 정산들을 모두 정산을 처리했다면, 더치페이 대표 정산도 완료되어야 함
        //대표 정산의 상태 확인
        Assertions.assertEquals(SettlementStatus.COMPLETE, dutchSettlement.getSettlementStatus());
    }


}