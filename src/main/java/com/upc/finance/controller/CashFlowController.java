package com.upc.finance.controller;

import com.upc.finance.model.dto.CashFlowResponseDto;
import com.upc.finance.service.impl.CashFlowServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cash Flow Controller")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class CashFlowController {

    private final CashFlowServiceImpl cashFlowService;
    public CashFlowController(CashFlowServiceImpl cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @Operation(summary = "Get cash flow by corporate bond ID")
    @RequestMapping("/cash-flow/{corporateBondId}")
    @Transactional
    public ResponseEntity<CashFlowResponseDto> getCashFlow(@PathVariable("corporateBondId") Long corporateBondId) {
        var res = cashFlowService.getCashFlowByCorporateBondId(corporateBondId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
