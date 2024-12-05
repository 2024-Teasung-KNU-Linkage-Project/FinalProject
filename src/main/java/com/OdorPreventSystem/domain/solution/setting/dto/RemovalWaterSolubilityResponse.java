package com.OdorPreventSystem.domain.solution.setting.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class RemovalWaterSolubilityResponse { //수용성 물질

    private final int idx;
    private final WaterSolubilityResponse waterSolubility;

}
