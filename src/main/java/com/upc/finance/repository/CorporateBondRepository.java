package com.upc.finance.repository;

import com.upc.finance.model.entity.CorporateBond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporateBondRepository extends JpaRepository<CorporateBond, Long> {

    List<CorporateBond> findAllByUserId(Long userId);

    List<CorporateBond> findAllByUserInvestor_Id(Long userInvestorId);
}
