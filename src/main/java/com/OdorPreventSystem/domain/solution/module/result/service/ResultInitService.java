package com.OdorPreventSystem.domain.solution.module.result.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.OdorPreventSystem.domain.solution.module.result.dto.FacilityCategoryResponse;
import com.OdorPreventSystem.domain.solution.module.result.repository.FacilityCategoryRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorCategoryRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorPreventionFacilityRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorSubstanceRepository;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.module.result.dto.OdorCategoryResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_FacilityResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_OdorResponse;

/*
 * ResultInitService:
 *     결과 탭에 표시할 데이터 초기화
 */
@Service
public class ResultInitService {

    private OdorCategoryRepository odorCategoryRepo;
    private OdorSubstanceRepository odorSubstanceRepo;
    private FacilityCategoryRepository facilityCategoryRepo;
    private OdorPreventionFacilityRepository odorPreventionFacilityRepo;

    public ResultInitService(OdorCategoryRepository odorCategoryRepo,
            OdorSubstanceRepository odorSubstanceRepo,
            FacilityCategoryRepository facilityCategoryRepo,
            OdorPreventionFacilityRepository odorPreventionFacilityRepo) {
        this.odorCategoryRepo = odorCategoryRepo;
        this.odorSubstanceRepo = odorSubstanceRepo;
        this.facilityCategoryRepo = facilityCategoryRepo;
        this.odorPreventionFacilityRepo = odorPreventionFacilityRepo;
    }

    /*
     * getOdorCategories:
     *     odors: 선택 악취방지시설 조합 적용 결과 악취기여물질 정보
     *     악취기여물질 분류와 분류에 해당하는 악취기여물질 저장하여 반환
     * 
     *  모든 악취 카테고리를 조회한 후, 각 카테고리에 속하는 악취 물질을 필터링하기 위해 filterResult_OdorByCategory 메서드를 호출 후 categories 리스트 반환
     */
    public List<OdorCategoryResponse> getOdorCategories(List<Result_OdorResponse> odors) {
        List<OdorCategoryResponse> categories = new ArrayList<>();
        odorCategoryRepo.findAll().forEach(i -> categories.add(
                    new OdorCategoryResponse(i.getIdx(), i.getName(),
                        filterResult_OdorByCategory(odors, i.getIdx()))));
        return categories;
    }

    /*
     * filterResult_OdorByCategory:
     *     odors에서 category에 속한 원소 필터링
     * 
     *  현재 카테고리 ID를 기준으로 odors 리스트에서 해당 카테고리에 속하는 악취 물질만을 필터링
     */
    private List<Result_OdorResponse> filterResult_OdorByCategory(
            List<Result_OdorResponse> odors, int category) {
        return odors.stream().filter(x -> odorSubstanceRepo
                .findById(x.getOdor().getIdx()).orElse(null).getCategory()
                .equals(odorCategoryRepo.findById(category).orElse(null)))
            .collect(Collectors.toList());
    }

    /*
     * getFacilityCategories:
     *     facilities: 선택 악취방지시설 조합 정보
     *     악취방지시설 분류와 해당하는 악취방지시설 저장하여 반환
     * 
     * 모든 시설 카테고리를 조회하고, 각 카테고리에 속하는 시설들을 필터링하여 FacilityCategoryResponse 리스트를 생성
     */
    public List<FacilityCategoryResponse> getFacilityCategories(
            List<Result_FacilityResponse> facilities) {
        List<FacilityCategoryResponse> categories = new ArrayList<>();
        facilityCategoryRepo.findAll().forEach(
                i -> categories.add(new FacilityCategoryResponse(
                        i.getIdx(), i.getName(),
                        filterResult_FacilityByCategory(facilities, i.getIdx()))));
        return categories;
    }

    /*
     * filterResult_FacilityByCategory:
     *     facilities에서 category에 속한 원소 필터링
     * 
     * 주어진 카테고리 ID를 기준으로 시설 리스트에서 해당 카테고리에 속하는 시설만을 필터링
     */
    private List<Result_FacilityResponse> filterResult_FacilityByCategory(
            List<Result_FacilityResponse> facilities, int category) {
        return facilities.stream().filter(x -> odorPreventionFacilityRepo
                .findById(x.getFacility().getIdx()).orElse(null).getCategory()
                .equals(facilityCategoryRepo.findById(category).orElse(null)))
            .collect(Collectors.toList());
    }

}
