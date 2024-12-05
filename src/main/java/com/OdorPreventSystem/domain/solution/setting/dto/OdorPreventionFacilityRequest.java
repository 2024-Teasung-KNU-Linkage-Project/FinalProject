package com.OdorPreventSystem.domain.solution.setting.dto;

import lombok.Data;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
public class OdorPreventionFacilityRequest { //악취방지시설 요청

    private int idx;
    private int basePrice;
    private long priceByWind;

}
