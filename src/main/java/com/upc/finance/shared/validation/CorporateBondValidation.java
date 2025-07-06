package com.upc.finance.shared.validation;

import com.upc.finance.model.dto.CorporateBondConfigUpdateRequestDto;
import com.upc.finance.model.dto.CorporateBondRequestDto;
import com.upc.finance.model.enums.GracePeriodType;
import com.upc.finance.model.enums.InterestRateType;
import com.upc.finance.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CorporateBondValidation {

    public void validateCorporateBond(CorporateBondRequestDto corporateBondRequestDto){
        if(corporateBondRequestDto.getNominalValue() == null || corporateBondRequestDto.getNominalValue() <= 0) {
            throw new ValidationException("The nominal value must be greater than zero");
        }
        if(corporateBondRequestDto.getCommercialValue() == null || corporateBondRequestDto.getCommercialValue() <= 0) {
            throw new ValidationException("The commercial value must be greater than zero");
        }
        if(corporateBondRequestDto.getInterestRate() == null || corporateBondRequestDto.getInterestRate() <= 0) {
            throw new ValidationException("Interest rate must be greater than zero");
        }
        if(corporateBondRequestDto.getInterestRateFrequency() == null || corporateBondRequestDto.getInterestRateFrequency() <= 0) {
            throw new ValidationException("Interest rate frequency cannot be null");
        }
        if(corporateBondRequestDto.getInterestRateType() == null) {
            throw new ValidationException("Interest rate type cannot be null");
        }
        if(corporateBondRequestDto.getInterestRateType() == InterestRateType.NOMINAL && corporateBondRequestDto.getInterestRateCapitalizationFrequency() == null) {
            throw new ValidationException("Capitalization cannot be null for nominal interest rate type");
        }
        if(corporateBondRequestDto.getCurrency() == null) {
            throw new ValidationException("Currency cannot be null");
        }
        if(corporateBondRequestDto.getPaymentPeriod() == null || corporateBondRequestDto.getPaymentPeriod() <= 0) {
            throw new ValidationException("Payment period must be greater than zero");
        }
        if(corporateBondRequestDto.getPaymentFrequency() == null || corporateBondRequestDto.getPaymentFrequency() <= 0) {
            throw new ValidationException("Payment frequency must be greater than zero");
        }
        if(corporateBondRequestDto.getRedemptionPremium() == null || corporateBondRequestDto.getRedemptionPremium() < 0) {
            throw new ValidationException("Redemption premium must be greater than or equal to zero");
        }
        if(corporateBondRequestDto.getCok() == null || corporateBondRequestDto.getCok() <= 0) {
            throw new ValidationException("Cost of capital (COK) must be greater than zero");
        }
        if(corporateBondRequestDto.getCokFrequency() == null || corporateBondRequestDto.getCokFrequency() <= 0) {
            throw new ValidationException("Cost of capital frequency must be greater than zero");
        }
        // Validate grace period type and months
        if(corporateBondRequestDto.getGracePeriodType() == GracePeriodType.TOTAL || corporateBondRequestDto.getGracePeriodType() == GracePeriodType.PARTIAL) {
            if(corporateBondRequestDto.getGracePeriods() == null || corporateBondRequestDto.getGracePeriods() <= 0) {
                throw new ValidationException("Grace periods must be greater than zero for total or partial grace period type");
            }
        } else if(corporateBondRequestDto.getGracePeriodType() == GracePeriodType.NONE) {
            corporateBondRequestDto.setGracePeriods(0);
        }
    }
    

    public void validateCapitalization(CorporateBondConfigUpdateRequestDto updateRequestDto) {
        // interest rate type is NOMINAL, capitalization cannot be null
        if (updateRequestDto.getInterestRateType() == InterestRateType.NOMINAL && updateRequestDto.getCapitalization() == null) {
            throw new ValidationException("Capitalization cannot be null for nominal interest rate type");
        }

    }
}
