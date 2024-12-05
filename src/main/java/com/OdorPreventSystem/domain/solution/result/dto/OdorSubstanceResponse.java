package com.OdorPreventSystem.domain.solution.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class OdorSubstanceResponse {
    
    private final int idx;
    private final String name;
    private final WaterSolubilityResponse waterSolubility; //수용성

}
