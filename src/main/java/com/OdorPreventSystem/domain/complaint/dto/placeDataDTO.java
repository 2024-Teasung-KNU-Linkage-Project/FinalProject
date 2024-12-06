package com.OdorPreventSystem.domain.complaint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class placeDataDTO {
    private Long companyIndex;
    private String name;
    private float latitude;
    private float longitude;
    private String odor;
    private String csvFilename;
}
