package com.OdorPreventSystem.domain.solution.module.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class InterferenceFactorResponse {

    private final int idx;
    private final String name;

}
