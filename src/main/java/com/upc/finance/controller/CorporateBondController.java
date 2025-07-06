package com.upc.finance.controller;

import com.upc.finance.model.dto.BondPurchaseRequestDto;
import com.upc.finance.model.dto.BondStateRequestDto;
import com.upc.finance.model.dto.CorporateBondRequestDto;
import com.upc.finance.model.dto.CorporateBondResponseDto;
import com.upc.finance.service.CorporateBondService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Corporate Bond Controller")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class CorporateBondController {

    private final CorporateBondService corporateBondService;


    public CorporateBondController(CorporateBondService corporateBondService) {
        this.corporateBondService = corporateBondService;
    }

    @Operation(summary = "Create a new corporate bond")
    @PostMapping("/corporate-bond")
    @Transactional
    public ResponseEntity<CorporateBondResponseDto> createCorporateBond(@RequestParam(name = "userId") Long userId,@RequestBody CorporateBondRequestDto corporateBondRequestDto) {
        var res = corporateBondService.createCorporateBond(userId, corporateBondRequestDto);
        System.out.println("Corporate bond created controller date: " + res.getIssueDate());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get corporate bond by ID")
    @GetMapping("/corporate-bond/{id:\\d+}")
    public ResponseEntity<CorporateBondResponseDto> getCorporateBondById(@PathVariable Long id) {
        var res = corporateBondService.getCorporateBondById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get all corporate bonds")
    @GetMapping("/corporate-bonds")
    public ResponseEntity<List<CorporateBondResponseDto>> getAllCorporateBonds() {
        var res = corporateBondService.getAllCorporateBonds();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get all corporate bonds by investor ID")
    @GetMapping("/corporate-bonds/investor/{investorId}")
    public ResponseEntity<List<CorporateBondResponseDto>> getAllInvestorByInvestorId(
            @PathVariable Long investorId) {
        var res = corporateBondService.getAllCorporateBondsByInvestorId(investorId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get all corporate bonds by user")
    @GetMapping("/corporate-bonds/user/{userId}")
    public ResponseEntity<List<CorporateBondResponseDto>> getAllCorporateBondsByUser(@PathVariable Long userId) {
        var res = corporateBondService.getAllCorporateBondsByUser(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Update corporate bond")
    @PutMapping("/corporate-bond/{id:\\d+}")
    @Transactional
    public ResponseEntity<CorporateBondResponseDto> updateCorporateBond(
            @PathVariable Long id,
            @RequestBody CorporateBondRequestDto corporateBondRequestDto) {
        System.out.println("Updating corporate bond with ID: " + id);
        var res = corporateBondService.updateCorporateBond(id, corporateBondRequestDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Delete corporate bond")
    @DeleteMapping("/corporate-bond/{id:\\d+}")
    @Transactional
    public ResponseEntity<Void> deleteCorporateBond(@PathVariable Long id) {
        System.out.println("Deleting corporate bond with ID: " + id);
        corporateBondService.deleteCorporateBond(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update corporate bond state")
    @PatchMapping("/corporate-bond/{id:\\d+}/state")
    @Transactional
    public ResponseEntity<CorporateBondResponseDto> updateState(
            @PathVariable Long id,
            @RequestBody BondStateRequestDto updateState) {
        var res = corporateBondService.updateCorporateBondState(id, updateState);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Update corporate bond state and investor")
    @PatchMapping("/corporate-bond/{id:\\d+}/investor")
    @Transactional
    public ResponseEntity<CorporateBondResponseDto> updateStateAndInvestor(
            @PathVariable Long id,
            @RequestBody BondPurchaseRequestDto updateBond) {
        var res = corporateBondService.updateStateAndInvestor(id, updateBond);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
