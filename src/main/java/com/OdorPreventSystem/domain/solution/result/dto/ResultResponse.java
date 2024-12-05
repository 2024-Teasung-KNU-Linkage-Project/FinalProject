package com.OdorPreventSystem.domain.solution.result.dto;

import java.util.List;
import java.util.ArrayList;

import lombok.Data;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
public class ResultResponse {

    private double theoreticalDillutionFactor; //이론적 희석배수
    private double targetDillutionFactor; //목표 희석배수
    private double expectedDillutionFactor; //예상 희석배수
    private double wind; //풍량

    private List<Result_OdorResponse> result_Odors = new ArrayList<>();
    private List<Result_InterferenceResponse> result_Interferences = new ArrayList<>();
    private List<Result_AdditionalResponse> result_Additionals = new ArrayList<>();
    private List<Result_FacilityResponse> result_Facilities = new ArrayList<>();

    public void addResult_Odor(Result_OdorResponse odor) {
        result_Odors.add(odor);
    }

    public void addResult_Interference(Result_InterferenceResponse interference) {
        result_Interferences.add(interference);
    }

    public void addResult_Additional(Result_AdditionalResponse additional) {
        result_Additionals.add(additional);
    }

    public void addResult_Facility(Result_FacilityResponse facility) {
        result_Facilities.add(facility);
    }
    
    //모든 시설의 단가 합산
    public double sumPrices() {
        double sum = 0;
        for (Result_FacilityResponse rf : result_Facilities) {
            sum += rf.getFacility().calculatePrice(wind);
        }
        return sum;
    }

}
