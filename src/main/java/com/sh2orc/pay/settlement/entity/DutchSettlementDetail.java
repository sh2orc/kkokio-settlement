package com.sh2orc.pay.settlement.entity;

import com.sh2orc.pay.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name="dutch_settlement_detail")
public class DutchSettlementDetail extends BaseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dutch_detail_id")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    //더치정산 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dutch_id", foreignKey = @ForeignKey(name = "fk_dutch_id"))
    private DutchSettlement dutchSettlement;

    //유저 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT) )
    private KkokioUser kkokioUser;

    //세부 정산 상태
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status")
    private SettlementStatus settlementStatus;

    //유저에게 할당된 정산 금액
    @Column(name = "settlement_amount")
    private Long settlementAmount;

    //유저에게 남은 정산 금액
    @Column(name = "unpaid_amount")
    private Long unpaidAmount;

    @Builder
    public DutchSettlementDetail(Long id, DutchSettlement dutchSettlement, KkokioUser kkokioUser, SettlementStatus settlementStatus, Long settlementAmount, Long unpaidAmount) {
        this.id = id;
        this.dutchSettlement = dutchSettlement;
        this.kkokioUser = kkokioUser;
        this.settlementStatus = settlementStatus;
        this.settlementAmount = settlementAmount;
        this.unpaidAmount = unpaidAmount;
    }

    public static DutchSettlementDetail of(DutchSettlement dutchSettlement, KkokioUser user, Long settlementAmount){

        return DutchSettlementDetail.builder()
            .dutchSettlement(dutchSettlement)
            .kkokioUser(user)
            .settlementAmount(settlementAmount)
            .unpaidAmount(settlementAmount)
            .settlementStatus(SettlementStatus.READY)
            .build();
    }

    public DutchSettlementDetail complete(){
        this.unpaidAmount = 0L;
        this.settlementStatus = SettlementStatus.COMPLETE;
        return this;
    }

    public DutchSettlementDetail update(Long unpaidAmount, SettlementStatus settlementStatus) {
        this.unpaidAmount = unpaidAmount;
        this.settlementStatus = settlementStatus;
        return this;
    }
}
