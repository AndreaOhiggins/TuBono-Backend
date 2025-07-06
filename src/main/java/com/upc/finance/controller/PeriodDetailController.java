package com.upc.finance.controller;

import com.upc.finance.model.entity.PeriodDetail;
import com.upc.finance.service.CashFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Period Detail Controller")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class PeriodDetailController {

    private final CashFlowService cashFlowService;

    public PeriodDetailController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @Operation(summary = "Get period details by cash flow ID")
    @GetMapping("/period-details/{cashFlowId}")
    public ResponseEntity<List<PeriodDetail>> getAllPeriodDetailsByCashFlowId(@PathVariable Long cashFlowId) {
        var res = cashFlowService.getAllPeriodDetailsByCashFlowId(cashFlowId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}



