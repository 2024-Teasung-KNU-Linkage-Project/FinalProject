package com.OdorPreventSystem.domain.solution.module.setting.dto;

import java.util.List;

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
    private List<RemovalEfficiencyResponse> efficiencies;

}
