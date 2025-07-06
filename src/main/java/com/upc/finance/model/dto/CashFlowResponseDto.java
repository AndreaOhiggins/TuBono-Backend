package com.upc.finance.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowResponseDto {
    private Long id;
    private Integer periodsNumber;
    private Double maxPrice;
    private Double convexity;
    private Double duration;
    private Double modifiedDuration;
    private Double tcea;
    private Double trea;
}
