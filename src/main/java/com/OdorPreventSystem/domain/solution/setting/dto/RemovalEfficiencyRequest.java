package com.OdorPreventSystem.domain.solution.setting.dto;

import lombok.Data;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
public class RemovalEfficiencyRequest { //효율요청

    private int idx;
    private float efficiency1;
    private float efficiency2;
    private float efficiency3;

}
