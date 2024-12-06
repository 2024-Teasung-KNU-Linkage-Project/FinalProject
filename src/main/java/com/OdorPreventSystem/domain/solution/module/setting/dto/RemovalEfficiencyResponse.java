package com.OdorPreventSystem.domain.solution.module.setting.dto;

import java.util.List;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class RemovalEfficiencyResponse { //효율응답

    private final int idx;
    private final List<RemovalInterferenceResponse> removalInterferences;
    private final List<RemovalWaterSolubilityResponse> removalSolubilities;

}
