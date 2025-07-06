package com.upc.finance.service;

import com.upc.finance.model.dto.BondPurchaseRequestDto;
import com.upc.finance.model.dto.BondStateRequestDto;
import com.upc.finance.model.dto.CorporateBondRequestDto;
import com.upc.finance.model.dto.CorporateBondResponseDto;

import java.util.List;

public interface CorporateBondService {

    public abstract CorporateBondResponseDto createCorporateBond(Long userId, CorporateBondRequestDto corporateBondRequestDto);
    public abstract CorporateBondResponseDto getCorporateBondById(Long id);
    public abstract List<CorporateBondResponseDto> getAllCorporateBondsByUser(Long userId);
    public abstract CorporateBondResponseDto updateCorporateBond(Long id, CorporateBondRequestDto corporateBondRequestDto);
    public abstract void deleteCorporateBond(Long id);
    public abstract CorporateBondResponseDto updateStateAndInvestor(Long id, BondPurchaseRequestDto updateBond);
    public abstract CorporateBondResponseDto updateCorporateBondState(Long id, BondStateRequestDto newState);

    public abstract List<CorporateBondResponseDto> getAllCorporateBondsByInvestorId(Long userInvestorId);

    public abstract List<CorporateBondResponseDto> getAllCorporateBonds();
}
