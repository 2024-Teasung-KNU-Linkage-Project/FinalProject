package com.OdorPreventSystem.domain.solution.module.setting.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
public class SettingRequest {

    private List<OdorSubstanceRequest> odorRequests = new ArrayList<>();
    private List<OdorPreventionFacilityRequest> facilityRequests = new ArrayList<>();
    private List<RemovalEfficiencyRequest> efficiencyRequests = new ArrayList<>();

    public void addOdor(OdorSubstanceRequest odor) {
        odorRequests.add(odor);
    }

    public void addFacility(OdorPreventionFacilityRequest facility) {
        facilityRequests.add(facility);
    }

    public void addEfficiency(RemovalEfficiencyRequest efficiency) {
        efficiencyRequests.add(efficiency);
    }

}
