package com.upc.finance.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upc.finance.model.enums.GracePeriodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "period_detail")
public class PeriodDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period", nullable = false)
    private Integer period;

    @Column(name = "nominal_value", nullable = false)
    private Double nominalValue;

    @Column(name = "interest", nullable = false)
    private Double interest;

    @Column(name = "amortization", nullable = false)
    private Double amortization;

    @Column(name = "payment", nullable = false)
    private Double payment;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "flow", nullable = false)
    private Double flow; // flujo final

    @Enumerated(EnumType.STRING)
    @Column(name = "grace_type", nullable = false)
    private GracePeriodType graceType; // TOTAL, PARTIAL, NONE

    @ManyToOne
    @JoinColumn(name = "idCashFlow", nullable = false, foreignKey = @ForeignKey(name="FK_cashFlowInterest_cashFlow"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CashFlow cashFlow;
}

