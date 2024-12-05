package com.OdorPreventSystem.domain.solution.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class Result_InterferenceResponse { //방해인자 
    
    private final InterferenceFactorResponse interference;
    private final double value;
    private final double targetValue; //목표값
    private final double expectedValue; //예상값

}
