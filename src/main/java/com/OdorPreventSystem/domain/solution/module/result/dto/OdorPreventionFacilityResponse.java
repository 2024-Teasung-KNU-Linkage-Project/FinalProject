package com.OdorPreventSystem.domain.solution.module.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class OdorPreventionFacilityResponse {

    private final int idx;
    private final String name;
    private final int basePrice; //기본 단가
    private final long priceByWind; //풍량에 따른 단가

    public double calculatePrice(double wind) {
        return basePrice + wind * priceByWind;
    }

}
