package com.sh2orc.pay.settlement.service;

import com.sh2orc.pay.settlement.dto.DutchPayDto;
import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.KkokioUser;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DutchPayApiServiceTest {

    /********************************************
     * Service
     *******************************************/
    @Autowired
    private DutchPayApiService dutchPayApiService;

    @Autowired
    private DutchPayProcessService dutchPayProcessService;


    /********************************************
     * Repository
     *******************************************/
    @Autowired //유저 계정
    private KKokioUserRepository userRepository;

    List<KkokioUser> participants;

    @BeforeEach
    @Transactional
    void setUp(){

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
    @Transactional
    @DisplayName("고객이 요청한 더치페이 정산 내역 조회")
    void getSettlementInfo() {
        //given
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 10000L);

        //when
        List<DutchPayDto.Settlement> settlementInfos = dutchPayApiService.getSettlementInfo(owner.getId());

        //then
        Assertions.assertEquals(2, settlementInfos.size());
        Assertions.assertEquals(60_000L, settlementInfos.stream().mapToLong(DutchPayDto.Settlement::getTotalAmount).sum());
    }

    @Test
    @Transactional
    @DisplayName("고객이 요청받은 정산 세부 내역 조회")
    void getSettlementDetail() {
        //given
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);

        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 30000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 30000L);

        //when
        List<DutchPayDto.SettlementDetail> settlementDetails = dutchPayApiService.getSettlementDetail(target1.getId());

        //then
        Assertions.assertEquals(2, settlementDetails.size());
        Assertions.assertEquals(20_000L, settlementDetails.stream().mapToLong(DutchPayDto.SettlementDetail::getSettlementAmount).sum());
    }
}