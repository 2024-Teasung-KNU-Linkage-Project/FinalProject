package com.OdorPreventSystem.domain.solution.result.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.OdorPreventSystem.domain.solution.result.dto.ResultRequest;
import com.OdorPreventSystem.domain.solution.result.dto.ResultResponse;
import com.OdorPreventSystem.domain.solution.result.dto.Result_AdditionalResponse;
import com.OdorPreventSystem.domain.solution.result.dto.Result_FacilityResponse;
import com.OdorPreventSystem.domain.solution.result.dto.Result_InterferenceResponse;
import com.OdorPreventSystem.domain.solution.result.dto.Result_OdorResponse;
import com.OdorPreventSystem.domain.solution.entity.AdditionalFeature;
import com.OdorPreventSystem.domain.solution.entity.InterferenceFactor;
import com.OdorPreventSystem.domain.solution.entity.OdorPreventionFacility;
import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;
import com.OdorPreventSystem.domain.solution.entity.Result;
import com.OdorPreventSystem.domain.solution.entity.Result_Additional;
import com.OdorPreventSystem.domain.solution.entity.Result_Facility;
import com.OdorPreventSystem.domain.solution.entity.Result_Interference;
import com.OdorPreventSystem.domain.solution.entity.Result_Odor;
import com.OdorPreventSystem.domain.solution.result.repository.AdditionalFeatureRepository;
import com.OdorPreventSystem.domain.solution.result.repository.InterferenceFactorRepository;
import com.OdorPreventSystem.domain.solution.result.repository.OdorPreventionFacilityRepository;
import com.OdorPreventSystem.domain.solution.result.repository.OdorSubstanceRepository;
import com.OdorPreventSystem.domain.solution.result.repository.ResultRepository;
import com.OdorPreventSystem.domain.solution.result.repository.Result_AdditionalRepository;
import com.OdorPreventSystem.domain.solution.result.repository.Result_FacilityRepository;
import com.OdorPreventSystem.domain.solution.result.repository.Result_InterferenceRepository;
import com.OdorPreventSystem.domain.solution.result.repository.Result_OdorRepository;

/*
 * ResultStoreService:
 *     입력과 결과 데이터 저장
 */
@Service
public class ResultStoreService {

    private final ResultRepository resultRepo;
    private final Result_OdorRepository result_OdorRepo;
    private final OdorSubstanceRepository odorSubstanceRepo;
    private final Result_InterferenceRepository result_InterferenceRepo;
    private final InterferenceFactorRepository interferenceFactorRepo;
    private final Result_AdditionalRepository result_AdditionalRepo;
    private final AdditionalFeatureRepository additionalFeatureRepo;
    private final Result_FacilityRepository result_FacilityRepo;
    private final OdorPreventionFacilityRepository odorPreventionFacilityRepo;

    public ResultStoreService(ResultRepository resultRepo,
            Result_OdorRepository result_OdorRepo,
            OdorSubstanceRepository odorSubstanceRepo,
            Result_InterferenceRepository result_InterferenceRepo,
            InterferenceFactorRepository interferenceFactorRepo,
            Result_AdditionalRepository result_AdditionalRepo,
            AdditionalFeatureRepository additionalFeatureRepo,
            Result_FacilityRepository result_FacilityRepo,
            OdorPreventionFacilityRepository odorPreventionFacilityRepo) {
        this.resultRepo = resultRepo;
        this.result_OdorRepo = result_OdorRepo;
        this.odorSubstanceRepo = odorSubstanceRepo;
        this.result_InterferenceRepo = result_InterferenceRepo;
        this.interferenceFactorRepo = interferenceFactorRepo;
        this.result_AdditionalRepo = result_AdditionalRepo;
        this.additionalFeatureRepo = additionalFeatureRepo;
        this.result_FacilityRepo = result_FacilityRepo;
        this.odorPreventionFacilityRepo = odorPreventionFacilityRepo;
    }

    /*
     * storedInDatabase:
     *     입력과 결과 데이터 데이터베이스에 저장
     */
    @Transactional
    public void storeInDatabase(ResultResponse resultResponse, ResultRequest resultRequest) {
        Result result = new Result(
                null, resultRequest.getName(), resultResponse.getTheoreticalDillutionFactor(),
                resultResponse.getTargetDillutionFactor(),
                resultResponse.getExpectedDillutionFactor(),
                resultResponse.getWind());
        resultRepo.save(result);

        for (Result_OdorResponse o : resultResponse.getResult_Odors()) {
            OdorSubstance odor = 
                odorSubstanceRepo.findById(o.getOdor().getIdx()).orElse(null);
            Result_Odor result_Odor = new Result_Odor(
                    null, result, odor, o.getValue(), o.getExpectedValue());
            result_OdorRepo.save(result_Odor);
        }

        for (Result_InterferenceResponse i : resultResponse.getResult_Interferences()) {
            InterferenceFactor interference =
                interferenceFactorRepo.findById(i.getInterference().getIdx())
                .orElse(null);
            Result_Interference result_Interference = new Result_Interference(
                    null, result, interference, i.getValue(),
                    i.getTargetValue(), i.getExpectedValue());
            result_InterferenceRepo.save(result_Interference);
        }

        for (Result_AdditionalResponse a : resultResponse.getResult_Additionals()) {
            AdditionalFeature additional = 
                additionalFeatureRepo.findById(a.getAdditional().getIdx())
                .orElse(null);
            Result_Additional result_Additional = new Result_Additional(
                    null, result, additional, a.isAvailable());
            result_AdditionalRepo.save(result_Additional);
        }

        for (Result_FacilityResponse f : resultResponse.getResult_Facilities()) {
            OdorPreventionFacility facility =
                odorPreventionFacilityRepo.findById(f.getFacility().getIdx())
                .orElse(null);
            Result_Facility result_Facility = new Result_Facility(
                    null, result, facility);
            result_FacilityRepo.save(result_Facility);
        }
    }

    /*
     * createContent:
     *     입력과 결과 데이터 CSV 파일 내용 생성
     */
    public String createContent(ResultResponse resultResponse) {
        String content = "";
        Date createdAt = new Date();

        content += "\ufeff";
        content += "생성일자: " + createdAt + "\n";
        content += "\n악취기여물질\n";
        content += "idx,이름,실측값,예측값,수용성여부\n";
        List<Result_OdorResponse> result_Odors
            = resultResponse.getResult_Odors();
        for (Result_OdorResponse ro : result_Odors) {
            content += ro.getOdor().getIdx() + ",";
            content += ro.getOdor().getName() + ",";
            content += ro.getValue() + ",";
            content += ro.getExpectedValue() + ",";
            if (ro.getOdor().getWaterSolubility().getIdx() == 0)
                content += "O\n";
            else
                content += "X\n";
        }

        content += "\n방해인자\n";
        content += "idx,이름,실측값,예측값\n";
        List<Result_InterferenceResponse> result_Interferences
            = resultResponse.getResult_Interferences();
        for (Result_InterferenceResponse ri : result_Interferences) {
            content += ri.getInterference().getIdx() + ",";
            content += ri.getInterference().getName() + ",";
            content += ri.getValue() + ",";
            content += ri.getExpectedValue() + "\n";
        }


        content += "\n희석배수\n";
        content += "이론적희석배수,목표희석배수,예측희석배수\n";
        content += resultResponse.getTheoreticalDillutionFactor() + ","
                   + resultResponse.getTargetDillutionFactor() + ","
                   + resultResponse.getExpectedDillutionFactor() + "\n";

        content += "\n악취방지시설 조합\n";
        content += "idx,이름,카테고리,단가\n";
        List<Result_FacilityResponse> result_Facilities
            = resultResponse.getResult_Facilities();
        for (Result_FacilityResponse rf : result_Facilities) {
            content += rf.getFacility().getIdx() + ",";
            content += rf.getFacility().getName() + ",";
            content += odorPreventionFacilityRepo.findById(rf.getFacility().getIdx()).orElse(null).getCategory().getName() + ",";
            content += rf.getFacility().calculatePrice(resultResponse.getWind()) + "\n";
        }

        content += ",,단가 총합," + resultResponse.sumPrices();

        return content;
    }

}
