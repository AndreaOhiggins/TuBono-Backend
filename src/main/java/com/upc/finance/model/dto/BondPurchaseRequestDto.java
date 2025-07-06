package com.upc.finance.model.dto;

import com.upc.finance.model.enums.BondState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BondPurchaseRequestDto {
    private BondState bondState;
    private Long userInvestorId;
}
