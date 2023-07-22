package com.sh2orc.pay.settlement.repository;

import com.sh2orc.pay.settlement.entity.KkokioUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KKokioUserRepository extends JpaRepository<KkokioUser, Long> {

}
