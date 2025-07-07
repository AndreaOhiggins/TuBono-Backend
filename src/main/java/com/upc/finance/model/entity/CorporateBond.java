package com.upc.finance.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.upc.finance.model.enums.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "corporate_bond")
public class CorporateBond {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private BondState state; // available, purchased

    @JsonProperty("issueDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "issueDate", nullable = false)
    private LocalDate issueDate;

    @Column(name = "nominal_value", nullable = false)
    private Double nominalValue;

    @Column(name = "commercial_value", nullable = false)
    private Double commercialValue;

    @Column(name = "interest_rate", nullable = false)
    private Double interestRate;

    @Column(name = "interest_rate_frequency", nullable = false) // days
    private Integer interestRateFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_rate_type", nullable = false)
    private InterestRateType interestRateType; // nominal, effective

    // depends if interest rate is nominal
    @Column(name = "interest_rate_capitalization_frequency") // days
    private Integer interestRateCapitalizationFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @Column(name = "payment_period", nullable = false)  // en años
    private Integer paymentPeriod;

    @Column(name = "payment_frequency", nullable = false)  // en dias, trimestre, semestral, cuatrimestral
    private Integer paymentFrequency;

    @Column(name = "redemption_premium", nullable = false)
    private Double redemptionPremium;

    @Column(name = "cok", nullable = false)
    private Double cok;

    @Column(name = "cok_frequency", nullable = false) // en días, frecuencia de la COK
    private Integer cokFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "grace_period_type", nullable = false)
    private GracePeriodType gracePeriodType;

    @Column(name = "grace_periods") // cantidad de periodos de gracia, si es 0 no hay
    private Integer gracePeriods;

    // initial costs
    @Column(name = "structuring_cost")
    private Double structuringCost;

    @Column(name = "placement_cost")
    private Double placementCost;

    @Column(name = "flotation_cost", nullable = false)
    private  Double flotationCost;

    @Column(name = "cavali_cost", nullable = false)
    private  Double cavaliCost;

    @ManyToOne
    @JoinColumn(name = "idUser", foreignKey = @ForeignKey(name = "FK_corporate_bond_user"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @ManyToOne
    @JoinColumn(name = "idUserInvestor", foreignKey = @ForeignKey(name = "FK_corporate_bond_user"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User userInvestor;

}