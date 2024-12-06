package com.OdorPreventSystem.domain.solution.module.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class Result_FacilityResponse {
    
    private final OdorPreventionFacilityResponse facility;

}
