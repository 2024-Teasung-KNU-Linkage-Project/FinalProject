package com.OdorPreventSystem.domain.solution.result.dto;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
//추가기능 응답
public class Result_AdditionalResponse {
    //final 이용한 불별 필드
    private final AdditionalFeatureResponse additional;
    private final boolean available;

}
