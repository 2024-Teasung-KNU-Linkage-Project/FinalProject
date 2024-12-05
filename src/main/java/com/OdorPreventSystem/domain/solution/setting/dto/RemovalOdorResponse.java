package com.OdorPreventSystem.domain.solution.setting.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class RemovalOdorResponse {  //악취제거

    private final int idx;
    private final OdorPreventionFacilityResponse facility;
    private final OdorSubstanceResponse odor;

}
