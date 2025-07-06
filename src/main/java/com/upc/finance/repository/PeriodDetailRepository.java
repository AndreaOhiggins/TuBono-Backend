package com.upc.finance.repository;

import com.upc.finance.model.entity.PeriodDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodDetailRepository extends JpaRepository<PeriodDetail, Long> {

    List<PeriodDetail> findAllByCashFlow_Id(Long cashFlowId);
}
