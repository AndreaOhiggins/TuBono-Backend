package com.upc.finance.model.dto;

import com.upc.finance.model.enums.CapitalizationFrequency;
import com.upc.finance.model.enums.CurrencyType;
import com.upc.finance.model.enums.InterestRateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorporateBondConfigUpdateRequestDto {
    private InterestRateType interestRateType;
    private CapitalizationFrequency capitalization;
    private CurrencyType currency;
}
