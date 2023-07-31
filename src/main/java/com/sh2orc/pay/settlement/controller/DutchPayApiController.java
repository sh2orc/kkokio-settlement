package com.sh2orc.pay.settlement.controller;

import com.sh2orc.pay.settlement.dto.ApiResponse;
import com.sh2orc.pay.settlement.dto.DutchPayDto;
import com.sh2orc.pay.settlement.service.DutchPayApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DutchPayApiController {

    private final DutchPayApiService dutchPayApiService;

    /**
     * 고객이 요청한 정산 내역 조회
     * Feature : 요청자는 자신이 요청한 정산하기 전체 리스트의 정보를 확인할 수 있습니다.
     * @param userId
     * @return
     */
    @GetMapping("/pay/dutch/request/info/{userId}")
    public ApiResponse<List<DutchPayDto.Settlement>>
        getDutchSettlements( @PathVariable("userId") Long userId){

        final var settlementInfos = dutchPayApiService.getSettlementInfo(userId);

        return ApiResponse.success(settlementInfos);
    }

    /**
     * 정산 요청 받은 내역 조회
     * Feature : 요청받은 사람은 자신이 요청받은 정산하기 전체 리스트의 정보를 확인할 수 있습니다
     * @param userId
     * @return
     */
    @GetMapping("/pay/dutch/pending/info/{userId}")
    public ApiResponse<List<DutchPayDto.SettlementDetail>>
        getDutchSettlementDetails(@PathVariable("userId") Long userId){

        final var settlementDetails = dutchPayApiService.getSettlementDetail(userId);

        return ApiResponse.success(settlementDetails);
    }

    /**
     * 고객이 더치페이 정산 요청/생성
     * Feature : 사용자는 다수의 사람들에게 금액을 지정하여 정산 요청을 할 수 있습니다.
     *
     * @param request DutchPayDto.RequestSettlementProcess
     * @return
     */
    @PostMapping("/pay/dutch/request")
    public ApiResponse<DutchPayDto.Settlement>
        requestDutchPay(@RequestBody DutchPayDto.RequestSettlementProcess request){

        return ApiResponse.success(null);
    }


    /**
     * 유저의 정산 처리
     * Feature : 요청받은 사용자는 정산하기 버튼을 통해서 요청한 요청자에게 금액을 송금할 수 있습니다.
     * @param request DutchPayDto.RequestDetailProcess
     * @return
     */
    @PostMapping("/pay/dutch/settlement")
    public ApiResponse<DutchPayDto.SettlementDetail>
        processDutchSettlementDetail(@RequestBody DutchPayDto.RequestDetailProcess request){

        return ApiResponse.success(null);
    }

}
