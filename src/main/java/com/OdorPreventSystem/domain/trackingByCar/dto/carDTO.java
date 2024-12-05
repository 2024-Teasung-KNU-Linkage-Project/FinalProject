package com.OdorPreventSystem.domain.trackingByCar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class carDTO {
    private Long carId;
    private float latitude;
    private float longitude;
    private String date;
    private String csvFilename;
    private float windDirection;



}
