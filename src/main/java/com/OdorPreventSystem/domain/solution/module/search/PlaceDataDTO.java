package com.OdorPreventSystem.domain.solution.module.search;

import com.OdorPreventSystem.domain.solution.module.company.entity.Company;
import lombok.*;

@Data
@NoArgsConstructor
public class PlaceDataDTO {
    private Long companyIndex;
    private String name;
    private String outlet;
    private float latitude;
    private float longitude;
    private String odor;
    private String csvFilename;

    @Builder
    public PlaceDataDTO(Long companyIndex, String name, String outlet, float latitude, float longitude, String odor, String csvFilename) {
        this.companyIndex = companyIndex;
        this.name = name;
        this.outlet = outlet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.odor = odor;
        this.csvFilename = csvFilename;
    }
}
