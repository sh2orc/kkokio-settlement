package com.sh2orc.pay.settlement.entity;

import com.sh2orc.pay.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name="dutch_settlement",
    indexes = {
        @Index(name = "ix_owner_user_id", columnList = "owner_user_id")
    }
)
public class DutchSettlement extends BaseAudit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dutch_id")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    // 정산 요청한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private KkokioUser ownerUser;

    // 정산금액
    @Column(name = "settlement_amount")
    private Long settlementAmount;

    // 더치페이 참여자수
    @Column(name = "dutch_people_count")
    private Integer dutchPeopleCount;

    // 나누기 정산금액 (1인당 정산금액)
    @Column(name = "divide_amount")
    private Long divideAmount;

    // 나머지 정산금액 (1/N 이후 나머지 꼬끼오가 지금하는 금액)
    @Column(name = "spare_amount")
    private Long spareAmount;

    // 정산 상태
    @Enumerated(value = EnumType.STRING)
    @Column(name = "settlement_status")
    private SettlementStatus settlementStatus;

    @Setter
    @OneToMany(mappedBy = "dutchSettlement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DutchSettlementDetail> settlementDetails=new ArrayList<>();

    @Builder
    public DutchSettlement(Long id, KkokioUser ownerUser, Long settlementAmount, Integer dutchPeopleCount, Long divideAmount, Long spareAmount, SettlementStatus settlementStatus, List<DutchSettlementDetail> settlementDetails) {
        this.id = id;
        this.ownerUser = ownerUser;
        this.settlementAmount = settlementAmount;
        this.dutchPeopleCount = dutchPeopleCount;
        this.divideAmount = divideAmount;
        this.spareAmount = spareAmount;
        this.settlementStatus = settlementStatus;
        this.settlementDetails = settlementDetails;
    }

    public static DutchSettlement of(KkokioUser owner, Integer dutchPeopleCount, Long settlementAmount){

        //더치페이 참여자 수 (+ owner)
        Integer countPeople = dutchPeopleCount;

        //1명당 더치페이 금액
        Long divideAmount = settlementAmount / countPeople;

        //나머지 정산금액 (회사 지급금액)
        Long sparseAmount = settlementAmount % countPeople;

        return DutchSettlement.builder()
                              .ownerUser(owner)
                              .dutchPeopleCount(countPeople)
                              .divideAmount(divideAmount)
                              .settlementAmount(settlementAmount)
                              .spareAmount(sparseAmount)
                              .settlementStatus(SettlementStatus.READY)
                              //.settlementDetails(null)
                              .build();
    }


    public DutchSettlement complete(){
        this.settlementStatus = SettlementStatus.COMPLETE;
        return this;
    }

    public DutchSettlement ongoing(){
        this.settlementStatus = SettlementStatus.ONGOING;
        return this;
    }

}
