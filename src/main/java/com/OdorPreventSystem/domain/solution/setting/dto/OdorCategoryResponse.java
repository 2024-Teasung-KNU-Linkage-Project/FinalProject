package com.OdorPreventSystem.domain.solution.setting.dto;

import java.util.List;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class OdorCategoryResponse { //악취 카테고리 응답

    private final int idx;
    private final String name;
    private final List<OdorSubstanceResponse> odors;

}
