package com.sh2orc.pay.settlement.repository;

import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.DutchSettlementDetail;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DutchSettlementDetailRepository extends JpaRepository<DutchSettlementDetail, Long> {
    Optional<List<DutchSettlementDetail>> findByDutchSettlement(DutchSettlement dutchSettlement);
    Optional<List<DutchSettlementDetail>> findByKkokioUser(KkokioUser kkokioUser);
    Optional<DutchSettlementDetail> findByDutchSettlementAndKkokioUser(DutchSettlement dutchSettlement, KkokioUser kkokioUser);

}
