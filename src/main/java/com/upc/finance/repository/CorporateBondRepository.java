package com.upc.finance.repository;

import com.upc.finance.model.entity.CorporateBond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorporateBondRepository extends JpaRepository<CorporateBond, Long> {

//    List<CorporateBond> findByUserId(Long userId);
    List<CorporateBond> findAllByUserId(Long userId);

    List<CorporateBond> findAllByUserInvestor_Id(Long userInvestorId);
}
