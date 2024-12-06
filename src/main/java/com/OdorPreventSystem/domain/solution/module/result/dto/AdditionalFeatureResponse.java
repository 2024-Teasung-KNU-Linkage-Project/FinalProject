package com.OdorPreventSystem.domain.solution.module.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)

//추가설비
public class AdditionalFeatureResponse {

    private final int idx;
    private final String name;

}
