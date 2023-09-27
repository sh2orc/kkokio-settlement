package com.sh2orc.pay.settlement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh2orc.pay.settlement.dto.ApiResponse;
import com.sh2orc.pay.settlement.dto.DutchPayDto;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import com.sh2orc.pay.settlement.repository.KKokioUserRepository;
import com.sh2orc.pay.settlement.service.DutchPayProcessService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DisplayName("더치페이 API 테스트")
@ActiveProfiles("test-integration")
public class DutchPayApiControllerTest {

    //로그 출력을 위한 객체 생성
    public Logger log = LoggerFactory.getLogger(DutchPayApiControllerTest.class);

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KKokioUserRepository userRepository;
    @Autowired
    private DutchPayProcessService dutchPayProcessService;

    private List<KkokioUser> participants;

    @BeforeEach
    @Transactional
    void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                      .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                                      .alwaysDo(MockMvcResultHandlers.print())
                                      .build();


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
    @DisplayName("고객이 요청한 정산 내역 조회 API")
    @Transactional
    void getDutchSettlements() throws Exception {

        //given
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1), 10000L);

        //when
        final String URI = "/pay/dutch/request/info/"+owner.getId();

        //Mock API 호출
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                      .get(URI)
                      .header(HttpHeaders.CONTENT_TYPE, "application/json")
             ).andExpect(MockMvcResultMatchers.status().isOk())
             .andDo(MockMvcResultHandlers.print())
             .andReturn();

        //리턴 Payload 객체 매핑
        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(result, ApiResponse.class);
        List<DutchPayDto.Settlement> data = (List<DutchPayDto.Settlement>) apiResponse.getData();

        //then
        Assertions.assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        Assertions.assertEquals("0000", apiResponse.getCode());
        Assertions.assertEquals(3, data.size());
    }

    @Test
    @DisplayName("고객이 요청받은 세부 정산 조회 API")
    @Transactional
    void getDutchSettlementDetails() throws Exception {

        //given
        KkokioUser owner = participants.get(0);
        KkokioUser target1 = participants.get(1);
        KkokioUser target2 = participants.get(2);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1, target2), 50000L);
        dutchPayProcessService.makeNewSettlement(owner, List.of(target1), 10000L);

        //when
        //정산을 요청 받은 target2에 대한 조회
        final String URI = "/pay/dutch/pending/info/"+target2.getId();
        //Mock API 호출
        MvcResult mvcResult = mockMvc.perform(
                                         MockMvcRequestBuilders
                                             .get(URI)
                                             .header(HttpHeaders.CONTENT_TYPE, "application/json")
                                     ).andExpect(MockMvcResultMatchers.status().isOk())
                                     .andDo(MockMvcResultHandlers.print())
                                     .andReturn();

        //리턴 Payload 객체 매핑
        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(result, ApiResponse.class);
        List<DutchPayDto.Settlement> data = (List<DutchPayDto.Settlement>) apiResponse.getData();

        //then
        Assertions.assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        Assertions.assertEquals("0000", apiResponse.getCode());
        Assertions.assertEquals(2, data.size());
    }
}