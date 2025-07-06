package com.upc.finance.service.strategy;

import com.upc.finance.model.entity.CashFlow;
import com.upc.finance.model.entity.CorporateBond;
import com.upc.finance.model.enums.Role;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.exception.NoBracketingException;

import java.util.List;

public class InvestorCashFlowCalculator implements CashFlowCalculatorStrategy{
    @Override
    public Role getSupportedRoles() {
        return Role.INVESTOR;
    }

    @Override
    public double calculate(CashFlow cashFlow, List<Double> flowList, Double cok, int paymentFrequency) {
        return calculateTREA(cashFlow, flowList, cok, paymentFrequency);
    }

    private Double calculateTREA(CashFlow cashFlow, List<Double> flowList, Double cok, int paymentFrequency) {
        // calculation for trea
        // TIR = calcularTIR(flow.stream().mapToDouble(Double::doubleValue).toArray());
        // TREA = (1 + TIR) ^ periodInOneYear - 1

        double tir = calcularTIR(flowList.stream().mapToDouble(Double::doubleValue).toArray());
        if (Double.isNaN(tir)) {
            return Double.NaN; // No se pudo calcular la TIR
        }
        var numPeriodsPerYear = 360 / paymentFrequency;
        return Math.pow(1 + tir, numPeriodsPerYear) - 1;
    }


    public double calcularTIR(double[] flujos) {
        for (int i = 0; i < flujos.length; i++) {
        }

        UnivariateFunction function = r -> {
            double npv = flujos[0];
            for (int i = 1; i < flujos.length; i++) {
                npv += flujos[i] / Math.pow(1 + r, i);
            }
            return npv;
        };

        BrentSolver solver = new BrentSolver(1.0e-12);
        try {
            // Intentamos buscar la TIR entre -0.99 y 100
            return solver.solve(100, function, -0.99, 100.0, 0);
        } catch (NoBracketingException e) {
            // No se encontró solución en el rango dado
            return Double.NaN;
        }
    }
}
