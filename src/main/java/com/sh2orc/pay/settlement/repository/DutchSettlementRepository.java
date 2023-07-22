package com.sh2orc.pay.settlement.repository;

import com.sh2orc.pay.settlement.entity.DutchSettlement;
import com.sh2orc.pay.settlement.entity.KkokioUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DutchSettlementRepository extends JpaRepository<DutchSettlement, Long> {
    Optional<List<DutchSettlement>> findByOwnerUser(KkokioUser user);

}
