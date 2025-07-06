package com.upc.finance.service.strategy;

import com.upc.finance.model.entity.CashFlow;
import com.upc.finance.model.entity.CorporateBond;
import com.upc.finance.model.enums.Role;

import java.util.List;

public interface CashFlowCalculatorStrategy {
    Role getSupportedRoles();
    public abstract double calculate(CashFlow cashFlow, List<Double> flow, Double cok, int paymentFrequency);
}
