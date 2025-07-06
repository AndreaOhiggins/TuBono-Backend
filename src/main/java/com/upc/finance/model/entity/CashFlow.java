package com.upc.finance.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cash_flow")
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="idCorporateBond", nullable = false, foreignKey = @ForeignKey(name="FK_cashflow_corporate_bond"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CorporateBond corporateBond;

    @Column(name = "periods_number")
    private Integer periodsNumber;  //results

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "convexity")
    private Double convexity;  //results

    @Column(name = "duration")
    private Double duration;  //results

    @Column(name = "modified_duration")
    private Double modifiedDuration;  //results

    @Column(name = "tcea") // null si es investor
    private Double tcea;

    @Column(name = "trea") // null si es issuer
    private Double trea;

}

