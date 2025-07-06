package com.upc.finance.repository;

import com.upc.finance.model.entity.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    Optional<Object> findByCorporateBond_Id(Long corporateBondId);
}
