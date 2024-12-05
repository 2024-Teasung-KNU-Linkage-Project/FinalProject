package com.OdorPreventSystem.domain.solution.result.dto;

import java.util.List;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class FacilityCategoryResponse {

    private final int idx;
    private final String name;
    private final List<Result_FacilityResponse> result_Facilities;

}
