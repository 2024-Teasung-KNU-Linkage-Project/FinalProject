package com.OdorPreventSystem.domain.solution.result.dto;

import java.util.List;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class OdorCategoryResponse {

    private final int idx;
    private final String name;
    private final List<Result_OdorResponse> result_Odors;

}
