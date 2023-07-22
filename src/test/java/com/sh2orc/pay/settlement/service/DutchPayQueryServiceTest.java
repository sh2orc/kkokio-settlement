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
public class DutchPayQueryServiceTest {

    /********************************************
     * Service
     *******************************************/
    @Autowired
    private DutchPayProcessService dutchPayProcessService;
    @Autowired
    private DutchPayQueryService dutchPayQueryService;


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
    @DisplayName("자신이 요청한 정산하기 전체 리스트의 정보 조회")
    @Transactional
    void getDutchSettlement() {
        /**------------------------------
         * given : 필요 데이터 설정 및 초기화
         *-------------------------------*/
        KkokioUser customer1 = participants.get(0);
        KkokioUser customer2 = participants.get(1);
        KkokioUser customer3 = participants.get(2);

        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2, customer3), 50000L);
        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2, customer3), 50000L);
        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2), 50000L);

        /**-----------------------------------
         * when : 더치 페이 요청한 내역 조회 호출
         *------------------------------------*/

        List<DutchSettlement> dutchSettlements = dutchPayQueryService.getDutchSettlement(customer1.getId());

        /**-----------------------------------
         * then : 처리 결과에 대한 검증
         *------------------------------------*/
        Assertions.assertEquals(3, dutchSettlements.size());

        //정산 오너가 고객1인지 체크
        Long customer1Count = dutchSettlements.stream()
                 .filter(settle -> settle.getOwnerUser().getId() == customer1.getId())
                 .count();

        Assertions.assertEquals(3, customer1Count);
    }

    @Test
    @DisplayName("요청 받은 사람은 자신이 요청 받은 정산 전체 리스트의 조회")
    @Transactional
    void getDutchDetails() {
        /**------------------------------
         * given : 필요 데이터 설정 및 초기화
         *-------------------------------*/
        KkokioUser customer1 = participants.get(0);
        KkokioUser customer2 = participants.get(1);
        KkokioUser customer3 = participants.get(2);

        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2, customer3), 50000L);
        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2, customer3), 50000L);
        dutchPayProcessService.makeNewSettlement(customer1, List.of(customer2), 50000L);


        /**-----------------------------------
         * when : 더치 페이 대상 세ㅇ 내역 조회 호출
         *------------------------------------*/

        List<DutchSettlementDetail> details2 = dutchPayQueryService.getDutchDetails(customer2.getId());
        List<DutchSettlementDetail> details3 = dutchPayQueryService.getDutchDetails(customer3.getId());

        /**-----------------------------------
         * then : 처리 결과에 대한 검증
         *------------------------------------*/
        Assertions.assertEquals(3, details2.size());
        Assertions.assertEquals(2, details3.size());
    }
}