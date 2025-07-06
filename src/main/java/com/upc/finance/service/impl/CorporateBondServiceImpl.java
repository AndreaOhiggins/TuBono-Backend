package com.upc.finance.service.impl;

import com.upc.finance.model.dto.BondPurchaseRequestDto;
import com.upc.finance.model.dto.BondStateRequestDto;
import com.upc.finance.model.dto.CorporateBondRequestDto;
import com.upc.finance.model.dto.CorporateBondResponseDto;
import com.upc.finance.model.entity.CorporateBond;
import com.upc.finance.model.enums.BondState;
import com.upc.finance.repository.CorporateBondRepository;
import com.upc.finance.repository.UserRepository;
import com.upc.finance.service.CashFlowService;
import com.upc.finance.service.CorporateBondService;
import com.upc.finance.shared.exception.ResourceNotFoundException;
import com.upc.finance.shared.validation.CorporateBondValidation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CorporateBondServiceImpl implements CorporateBondService {


    private final CorporateBondRepository corporateBondRepository;
    private final ModelMapper modelMapper;
    private final CorporateBondValidation corporateBondValidation;
    private final UserRepository userRepository;
    private final CashFlowService cashFlowService;

    public CorporateBondServiceImpl(CorporateBondRepository corporateBondRepository, ModelMapper modelMapper, CorporateBondValidation corporateBondValidation, UserRepository userRepository, CashFlowService cashFlowService) {
        this.corporateBondRepository = corporateBondRepository;
        this.modelMapper = modelMapper;
        this.corporateBondValidation = corporateBondValidation;
        this.userRepository = userRepository;
        this.cashFlowService = cashFlowService;
    }


    @Override
    public CorporateBondResponseDto createCorporateBond(Long userId, CorporateBondRequestDto corporateBondRequestDto) {

        // General validation
        corporateBondValidation.validateCorporateBond(corporateBondRequestDto);

        // Validate user
        var user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        var bond = modelMapper.map(corporateBondRequestDto, CorporateBond.class);
        bond.setUser(user);
//        String issueDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String issueDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//        bond.setIssueDate(LocalDate.parse(issueDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bond.setIssueDate(LocalDate.parse(issueDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        bond.setState(BondState.AVAILABLE);

        System.out.println("---------------------------------");
        System.out.println("About to save this bond: " + bond);
        System.out.println("---------------------------------");
        var createdBond = corporateBondRepository.save(bond);

        // call generateCashFlow method to create cash flow for the bond
        //
        cashFlowService.generateCashFlow(userId, bond);

        return modelMapper.map(createdBond, CorporateBondResponseDto.class);
    }

    @Override
    public CorporateBondResponseDto getCorporateBondById(Long id) {
        var corporateBond = corporateBondRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corporate bond not found with id: " + id));
        return modelMapper.map(corporateBond, CorporateBondResponseDto.class);
    }

    @Override
    public List<CorporateBondResponseDto> getAllCorporateBonds() {
        var corporateBonds = corporateBondRepository.findAll();

        // validate if empty
        if (corporateBonds.isEmpty()) {
            throw new ResourceNotFoundException("No corporate bonds found");
        }

        return corporateBonds.stream()
                .map(bond -> modelMapper.map(bond, CorporateBondResponseDto.class))
                .toList();
    }

    @Override
    public List<CorporateBondResponseDto> getAllCorporateBondsByUser(Long userId) {
        var corporateBonds = corporateBondRepository.findAllByUserId(userId);

        // validate if empty
        if (corporateBonds.isEmpty()) {
            throw new ResourceNotFoundException("No corporate bonds found for user with id: " + userId);
        }

        return corporateBonds.stream()
                .map(bond -> modelMapper.map(bond, CorporateBondResponseDto.class))
                .toList();
    }

    @Override
    public CorporateBondResponseDto updateCorporateBond(Long id, CorporateBondRequestDto corporateBondRequestDto) {
        var corporateBond = corporateBondRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate bond not found with id: " + id));

        // General validation
        corporateBondValidation.validateCorporateBond(corporateBondRequestDto);

        // Update info
        modelMapper.map(corporateBondRequestDto, corporateBond);
        var savedBond = corporateBondRepository.save(corporateBond);

        return modelMapper.map(savedBond, CorporateBondResponseDto.class);
    }

    @Override
    public void deleteCorporateBond(Long id) {
        if (!corporateBondRepository.existsById(id))
            throw new ResourceNotFoundException("Corporate bond not found with id: " + id);

        corporateBondRepository.deleteById(id);
    }

    @Override
    public CorporateBondResponseDto updateStateAndInvestor(Long id, BondPurchaseRequestDto updateBond) {
        var corporateBond = corporateBondRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate bond not found with id: " + id));

        // Validate user
        var user = userRepository.findUserById(updateBond.getUserInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + updateBond.getUserInvestorId()));

        // Validate new state
        System.out.println("Updating corporate bond state to: " + corporateBond.getState() + "to : " + updateBond.getBondState());
        if (corporateBond.getState() == null) {
            throw new IllegalArgumentException("Invalid bond state: " + updateBond.getBondState());
        }

        // Update investor
        corporateBond.setUserInvestor(user);
        if (updateBond.getBondState() == BondState.AVAILABLE) {
            corporateBond.setState(BondState.AVAILABLE);
        } else if (updateBond.getBondState() == BondState.PURCHASED) {
            corporateBond.setState(BondState.PURCHASED);
        } else {
            throw new IllegalArgumentException("Invalid bond state: " + updateBond.getBondState());
        }

        var updatedBond = corporateBondRepository.save(corporateBond);
        return modelMapper.map(updatedBond, CorporateBondResponseDto.class);
    }

    @Override
    public CorporateBondResponseDto updateCorporateBondState(Long id, BondStateRequestDto newState) {
        var corporateBond = corporateBondRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate bond not found with id: " + id));

        System.out.println("corporate bond before updating state: " + corporateBond);
        // Validate new state

        System.out.println("Updating corporate bond state to: " + corporateBond.getState() + "to : " + newState);
        if (corporateBond.getState() == null) {
            throw new IllegalArgumentException("Invalid bond state: " + newState);
        }

        // Update state

        if (newState.getState() == BondState.AVAILABLE) {
            corporateBond.setState(BondState.AVAILABLE);
        } else if (newState.getState() == BondState.PURCHASED) {
            corporateBond.setState(BondState.PURCHASED);
        } else {
            throw new IllegalArgumentException("Invalid bond state: " + newState.getState());
        }
        var updatedBond = corporateBondRepository.save(corporateBond);

        return modelMapper.map(updatedBond, CorporateBondResponseDto.class);
    }

    @Override
    public List<CorporateBondResponseDto> getAllCorporateBondsByInvestorId(Long userInvestorId) {
        var corporateBonds = corporateBondRepository.findAllByUserInvestor_Id(userInvestorId);

        // validate if empty
        if (corporateBonds.isEmpty()) {
            throw new ResourceNotFoundException("No corporate bonds found for investor with id: " + userInvestorId);
        }

        return corporateBonds.stream()
                .map(bond -> modelMapper.map(bond, CorporateBondResponseDto.class))
                .toList();
    }


}
