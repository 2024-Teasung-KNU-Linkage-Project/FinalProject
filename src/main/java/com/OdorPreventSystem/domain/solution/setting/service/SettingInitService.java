package com.OdorPreventSystem.domain.solution.setting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.OdorPreventSystem.domain.solution.setting.respository.RemovalEfficiencyRepository;
import com.OdorPreventSystem.domain.solution.setting.respository.RemovalInterferenceRepository;
import com.OdorPreventSystem.domain.solution.setting.respository.RemovalWaterSolubilityRepository;
import com.OdorPreventSystem.domain.solution.setting.respository.WaterSolubilityRepository;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.setting.dto.FacilityCategoryResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.InterferenceFactorResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.OdorCategoryResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.OdorPreventionFacilityRequest;
import com.OdorPreventSystem.domain.solution.setting.dto.OdorPreventionFacilityResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.OdorSubstanceRequest;
import com.OdorPreventSystem.domain.solution.setting.dto.OdorSubstanceResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.RemovalEfficiencyRequest;
import com.OdorPreventSystem.domain.solution.setting.dto.RemovalEfficiencyResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.RemovalInterferenceResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.RemovalWaterSolubilityResponse;
import com.OdorPreventSystem.domain.solution.setting.dto.SettingRequest;
import com.OdorPreventSystem.domain.solution.setting.dto.WaterSolubilityRequest;
import com.OdorPreventSystem.domain.solution.setting.dto.WaterSolubilityResponse;
import com.OdorPreventSystem.domain.solution.entity.OdorPreventionFacility;
import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;
import com.OdorPreventSystem.domain.solution.setting.entity.RemovalEfficiency;
import com.OdorPreventSystem.domain.solution.setting.entity.RemovalInterference;
import com.OdorPreventSystem.domain.solution.setting.entity.RemovalWaterSolubility;
import com.OdorPreventSystem.domain.solution.result.repository.FacilityCategoryRepository;
import com.OdorPreventSystem.domain.solution.result.repository.InterferenceFactorRepository;
import com.OdorPreventSystem.domain.solution.result.repository.OdorCategoryRepository;
import com.OdorPreventSystem.domain.solution.result.repository.OdorPreventionFacilityRepository;
import com.OdorPreventSystem.domain.solution.result.repository.OdorSubstanceRepository;

/*
 * SettingInitService:
 *     설정 탭에 표시할 데이터 초기화
 */
@Service
public class SettingInitService { 

    private final OdorCategoryRepository odorCategoryRepo;
    private final OdorSubstanceRepository odorSubstanceRepo;
    private final InterferenceFactorRepository interferenceFactorRepo;
    private final FacilityCategoryRepository facilityCategoryRepo;
    private final OdorPreventionFacilityRepository odorPreventionFacilityRepo;
    private final WaterSolubilityRepository waterSolubilityRepo;
    private final RemovalEfficiencyRepository removalEfficiencyRepo;
    private final RemovalInterferenceRepository removalInterferenceRepo;
    private final RemovalWaterSolubilityRepository removalWaterSolubilityRepo;

    public SettingInitService(
                OdorCategoryRepository odorCategoryRepo,
                OdorSubstanceRepository odorSubstanceRepo,
                InterferenceFactorRepository interferenceFactorRepo,
                FacilityCategoryRepository facilityCategoryRepo,
                OdorPreventionFacilityRepository odorPreventionFacilityRepo,
                WaterSolubilityRepository waterSolubilityRepo,
                RemovalEfficiencyRepository removalEfficiencyRepo,
                RemovalInterferenceRepository removalInterferenceRepo,
                RemovalWaterSolubilityRepository removalWaterSolubilityRepo) {
        this.odorCategoryRepo = odorCategoryRepo;
        this.odorSubstanceRepo = odorSubstanceRepo;
        this.interferenceFactorRepo = interferenceFactorRepo;
        this.facilityCategoryRepo = facilityCategoryRepo;
        this.odorPreventionFacilityRepo = odorPreventionFacilityRepo;
        this.waterSolubilityRepo = waterSolubilityRepo;
        this.removalEfficiencyRepo = removalEfficiencyRepo;
        this.removalInterferenceRepo = removalInterferenceRepo;
        this.removalWaterSolubilityRepo = removalWaterSolubilityRepo;
    }

    /*
     * getODorCategories:
     *     악취기여물질 분류와 분류에 해당하는 악취기여물질 저장하여 반환
     */
    public List<OdorCategoryResponse> getOdorCategories() {
        List<OdorCategoryResponse> categories = new ArrayList<>();
        odorCategoryRepo.findAll().forEach(i -> categories.add(
                    new OdorCategoryResponse(i.getIdx(), i.getName(),
                        findOdorsByCategory(i.getIdx()))));
        return categories;
    }

    /*
     * getODorCategories:
     *     category에 속한 악취기여물질 검색
     */
    private List<OdorSubstanceResponse> findOdorsByCategory(int category) {
        List<OdorSubstance> odors = StreamSupport.stream(odorSubstanceRepo
                .findAll().spliterator(), false)
            .filter(x -> x.getCategory().equals(odorCategoryRepo
                        .findById(category).orElse(null)))
            .collect(Collectors.toList());
        List<OdorSubstanceResponse> odorResponses = new ArrayList<>();
        odors.forEach(i -> odorResponses.add(new OdorSubstanceResponse(
                        i.getIdx(), i.getName(), new WaterSolubilityResponse(
                            i.getWaterSolubility().getIdx(),
                            i.getWaterSolubility().getName()))));
        return odorResponses;
    }

    /*
     * getWaterSolubilities:
     *     수용성, 비수용성 반환
     */
    public List<WaterSolubilityResponse> getWaterSolubilities() {
        List<WaterSolubilityResponse> waterSolubilities = new ArrayList<>();
        waterSolubilityRepo.findAll().forEach(
                i -> waterSolubilities.add(new WaterSolubilityResponse(
                        i.getIdx(), i.getName())));
        return waterSolubilities;
    }

    /*
     * getFacilityCategories:
     *     악취방지시설 분류와 해당하는 악취방지시설 저장하여 반환
     */
    public List<FacilityCategoryResponse> getFacilityCategories() {
        List<FacilityCategoryResponse> categories = new ArrayList<>();
        facilityCategoryRepo.findAll().forEach(
                i -> categories.add(new FacilityCategoryResponse(
                        i.getIdx(), i.getName(),
                        findFacilitiesByCategory(i.getIdx()))));
        return categories;
    }

    /*
     * findFacilitiesByCategory:
     *     category에 속한 악취방지시설 검색
     */
    private List<OdorPreventionFacilityResponse> findFacilitiesByCategory(
        int category) {
        List<OdorPreventionFacility> facilities = StreamSupport
            .stream(odorPreventionFacilityRepo.findAll().spliterator(), false)
            .filter(x -> x.getCategory().equals(facilityCategoryRepo
                        .findById(category).orElse(null)))
            .collect(Collectors.toList());
        List<OdorPreventionFacilityResponse> facilityResponses 
            = new ArrayList<>();
        facilities.forEach(i -> facilityResponses
                .add(new OdorPreventionFacilityResponse(i.getIdx(),
                        i.getName(), findEfficiencyResponsesByFacility(i.getIdx()))));
        return facilityResponses;
    }

    /*
     * findEfficiencyResponsesByFacility:
     *     facility에 속한 제거효율 검색
     */
    private List<RemovalEfficiencyResponse> findEfficiencyResponsesByFacility(
        int facility) {
        List<RemovalEfficiency> efficiencies = StreamSupport
            .stream(removalEfficiencyRepo.findAll().spliterator(), false)
            .filter(x -> x.getFacility().equals(odorPreventionFacilityRepo
                        .findById(facility).orElse(null)))
            .collect(Collectors.toList());
        List<RemovalEfficiencyResponse> efficiencyResponses = new ArrayList<>();
        int category = odorPreventionFacilityRepo.findById(facility)
            .orElse(null).getCategory().getIdx();
        switch (category) {
            case 1: case 3:
                efficiencies.forEach(i -> efficiencyResponses.add(
                            new RemovalEfficiencyResponse(i.getIdx(), 
                                findRemovalInterferencesByEfficiency(
                                    i.getIdx()), null)));
                break;
            case 2: case 4:
                efficiencies.forEach(i -> efficiencyResponses.add(
                            new RemovalEfficiencyResponse(i.getIdx(), null,
                                findRemovalWaterSolubilitiesByEfficiency(
                                    i.getIdx()))));
                break;
        }
        return efficiencyResponses;
    }

    /*
     * findRemovalInterferencesByEfficiency:
     *     efficiency에 속한 제거방해인자 검색
     */
	private List<RemovalInterferenceResponse>
        findRemovalInterferencesByEfficiency(int efficiency) {
        List<RemovalInterference> removalInterferences = StreamSupport
            .stream(removalInterferenceRepo.findAll().spliterator(), false)
            .filter(x -> x.getEfficiency().equals(removalEfficiencyRepo
                        .findById(efficiency).orElse(null)))
            .collect(Collectors.toList());
        List<RemovalInterferenceResponse> removalInterferenceResponses
            = new ArrayList<>();
        removalInterferences.forEach(i -> removalInterferenceResponses
                .add(new RemovalInterferenceResponse(i.getIdx(),
                        new InterferenceFactorResponse(
                            i.getInterference().getIdx(),
                            i.getInterference().getName()))));
        return removalInterferenceResponses;
    }

    /*
     * findRemovalWaterSolubilitiesByEfficiency:
     *     efficiency에 속한 제거 수용성 여부 검색
     */
    private List<RemovalWaterSolubilityResponse>
findRemovalWaterSolubilitiesByEfficiency(int efficiency) {
        List<RemovalWaterSolubility> removalSolubilities = StreamSupport
            .stream(removalWaterSolubilityRepo.findAll().spliterator(), false)
            .filter(x -> x.getEfficiency().equals(removalEfficiencyRepo
                        .findById(efficiency).orElse(null)))
            .collect(Collectors.toList());
        List<RemovalWaterSolubilityResponse> removalSolubilityResponses
            = new ArrayList<>();
        removalSolubilities.forEach(i -> removalSolubilityResponses
                .add(new RemovalWaterSolubilityResponse(i.getIdx(),
                        new WaterSolubilityResponse(
                            i.getWaterSolubility().getIdx(),
                            i.getWaterSolubility().getName()))));
        return removalSolubilityResponses;
    }

    //?
    public SettingRequest initSettingRequest() {
        SettingRequest setting = new SettingRequest();
        odorSubstanceRepo.findAll().forEach(i -> setting
                .addOdor(new OdorSubstanceRequest(i.getIdx(),
                        i.getThreshold(), new WaterSolubilityRequest(
                            i.getWaterSolubility().getIdx()))));
        odorPreventionFacilityRepo.findAll().forEach(i -> setting
                .addFacility(new OdorPreventionFacilityRequest(
                        i.getIdx(), i.getBasePrice(), i.getPriceByWind())));
        removalEfficiencyRepo.findAll().forEach(i -> setting
                .addEfficiency(new RemovalEfficiencyRequest(i.getIdx(),
                        i.getEfficiency1(), i.getEfficiency2(),
                        i.getEfficiency3())));
        return setting;
    }

}
