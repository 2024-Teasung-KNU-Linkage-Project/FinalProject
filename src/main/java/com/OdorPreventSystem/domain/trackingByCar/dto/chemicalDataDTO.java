package com.OdorPreventSystem.domain.trackingByCar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class chemicalDataDTO {
    private String chemicalName;
    private float chemicalValue;
    private float msv;
}
