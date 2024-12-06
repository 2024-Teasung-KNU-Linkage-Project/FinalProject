package com.OdorPreventSystem.domain.solution.module.setting.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class InterferenceFactorResponse { //방해인자응답

    private final int idx;
    private final String name;

}
