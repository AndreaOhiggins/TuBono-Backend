package com.upc.finance.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.upc.finance.model.enums.CapitalizationFrequency;
import com.upc.finance.model.enums.CurrencyType;
import com.upc.finance.model.enums.GracePeriodType;
import com.upc.finance.model.enums.InterestRateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorporateBondResponseDto {

    private Long id;

    private String name;

    private String state; // available, purchased

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate issueDate;

    private Double nominalValue;
    private Double commercialValue;
    private Double interestRate;
    private Integer interestRateFrequency;      // days
    private InterestRateType interestRateType; // nominal, effective

    // depends if interest rate is nominal
    private Integer interestRateCapitalizationFrequency;   // days

    private CurrencyType currency;
    private Integer paymentPeriod;      // in years
    private Integer paymentFrequency;            // en dias, trimestre, semestral, cuatrimestral
    private Double redemptionPremium;
    private Double cok;
    private Integer cokFrequency;        // frecuencia de la COK

    private GracePeriodType gracePeriodType;
    private Integer gracePeriods;   // cantidad de periodos de gracia, si es 0 no hay

    // initial costs
    private Double structuringCost;
    private Double placementCost;
    private  Double flotationCost;
    private  Double cavaliCost;

    private Long userInvestorId;
}
