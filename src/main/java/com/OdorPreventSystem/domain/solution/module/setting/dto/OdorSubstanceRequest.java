package com.OdorPreventSystem.domain.solution.module.setting.dto;

import lombok.Data;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
public class OdorSubstanceRequest {

    private int idx;
    private double threshold;
    private WaterSolubilityRequest waterSolubility;

}
