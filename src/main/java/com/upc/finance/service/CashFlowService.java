package com.upc.finance.service;

import com.upc.finance.model.dto.CashFlowResponseDto;
import com.upc.finance.model.dto.CorporateBondResponseDto;
import com.upc.finance.model.entity.CashFlow;
import com.upc.finance.model.entity.CorporateBond;
import com.upc.finance.model.entity.PeriodDetail;

import java.util.List;

public interface CashFlowService {
    public abstract CashFlow generateCashFlow(Long userId, CorporateBond corporateBond);
    public abstract CashFlowResponseDto getCashFlowByCorporateBondId(Long corporateBondId);
    public abstract List<PeriodDetail> getAllPeriodDetailsByCashFlowId(Long cashFlowId);
}
