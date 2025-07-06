package com.upc.finance.repository;

import com.upc.finance.model.entity.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    Optional<Object> findByCorporateBond_Id(Long corporateBondId);
}
