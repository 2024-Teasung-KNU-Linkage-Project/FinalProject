package com.OdorPreventSystem.domain.trackingByCar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class placeDataDTO {
    private Long SourceId;
    private String name;
    private float latitude;
    private float longitude;
    private String odor;
    private String csvFilename;
}
