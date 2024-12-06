package com.OdorPreventSystem.domain.solution.module.setting.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class RemovalInterferenceResponse { //방해인자

    private final int idx;
    private final InterferenceFactorResponse interference;

}
