package com.OdorPreventSystem.domain.solution.module.search;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChemicalDataDTO {
    private String chemicalName; // 화학 물질 이름
    private BigDecimal chemicalValue; // 화학 물질 농도
    private float msv; // MSV (Maximum Safe Value)
    private String chemicalNameKr; 
}

