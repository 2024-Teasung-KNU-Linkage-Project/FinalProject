package com.OdorPreventSystem.domain.solution.module.result.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.OdorPreventSystem.domain.solution.module.result.dto.AdditionalFeatureResponse;
import com.OdorPreventSystem.domain.solution.module.result.repository.InterferenceFactorRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorPreventionFacilityRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorSubstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityCombination;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto.CombinationResponse;
//import com.prooffacilitysystem.dto.predict.FacilityDto.Entity;
//import com.prooffacilitysystem.dto.predict.FacilityDto.FindCombination;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency.InfEff;
//import com.prooffacilitysystem.dto.result.FacilityCategoryResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.InterferenceFactorResponse;
//import com.prooffacilitysystem.dto.result.OdorCategoryResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.OdorPreventionFacilityResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.OdorSubstanceResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.ResultResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_AdditionalResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_FacilityResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_InterferenceResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.Result_OdorResponse;
import com.OdorPreventSystem.domain.solution.module.result.dto.WaterSolubilityResponse;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.AdditionalFeatureDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InterferenceFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto.Response;
import com.OdorPreventSystem.domain.solution.entity.AdditionalFeature;
import com.OdorPreventSystem.domain.solution.entity.FacilityCategory;
import com.OdorPreventSystem.domain.solution.entity.InterferenceFactor;
//import com.prooffacilitysystem.entity.OdorCategory;
import com.OdorPreventSystem.domain.solution.entity.OdorPreventionFacility;
import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;
import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;
import com.OdorPreventSystem.domain.solution.module.result.repository.AdditionalFeatureRepository;

/*
 * ResultPassService:
 *     예측설비 탭에서 결과 탭으로 정보 전달 매개
 */
@Service
public class ResultPassService {
	
	@Autowired
	private OdorProofSystemMapper odorProofSystemMapper;

    private final OdorSubstanceRepository odorSubstanceRepo;
    private final InterferenceFactorRepository interferenceFactorRepo;
    private final AdditionalFeatureRepository additionalFeatureRepo;
    private final OdorPreventionFacilityRepository odorPreventionFacilityRepo;
	//리포지토리 객체들을 초기화
    public ResultPassService(
            OdorSubstanceRepository odorSubstanceRepo,
            InterferenceFactorRepository interferenceFactorRepo,
            AdditionalFeatureRepository additionalFeatureRepo,
            OdorPreventionFacilityRepository odorPreventionFacilityRepo) {
        this.odorSubstanceRepo = odorSubstanceRepo;
        this.interferenceFactorRepo = interferenceFactorRepo;
        this.additionalFeatureRepo = additionalFeatureRepo;
        this.odorPreventionFacilityRepo = odorPreventionFacilityRepo;
    }

    /*
     * addOdor:
     *     선택 악취방지시설 조합 적용 결과 악취기여물질 추가
     */
    private void addOdor(ResultResponse result, int idx,
            double value, double expectedValue) {
        OdorSubstance o = odorSubstanceRepo.findById(idx).orElse(null);
        OdorSubstanceResponse odorResponse = new OdorSubstanceResponse(
                idx, o.getName(), new WaterSolubilityResponse(
                    o.getWaterSolubility().getIdx()));
        result.addResult_Odor(new Result_OdorResponse(odorResponse, value,
                    expectedValue));
    }

    /*
     * addInterference:
     *     선택 악취방지시설 조합 적용 결과 방해인자 추가
     */
    private void addInterference(
            ResultResponse result, int idx, double value,
            double targetValue, double expectedValue) {
        InterferenceFactor i =
            interferenceFactorRepo.findById(idx).orElse(null);
        InterferenceFactorResponse interferenceResponse = 
            new InterferenceFactorResponse(idx, i.getName());
        result.addResult_Interference(new Result_InterferenceResponse(
                    interferenceResponse, value, targetValue, expectedValue));
    }

    /*
     * addAdditional:
     *     사용자가 입력한 추가설비 사용 여부 추가
     */
    private void addAdditional(
            ResultResponse result, int idx, boolean available) {
        AdditionalFeature a
            = additionalFeatureRepo.findById(idx).orElse(null);
        AdditionalFeatureResponse additionalResponse =
            new AdditionalFeatureResponse(idx, a.getName());
        result.addResult_Additional(new Result_AdditionalResponse(additionalResponse,
                    available));
    }

    /*
     * addFacility:
     *     선택 악취방지시설 조합의 악취방지시설 추가
     */
    private void addFacility(
            ResultResponse result, int idx) {
        OdorPreventionFacility f =
            odorPreventionFacilityRepo.findById(idx).orElse(null);
        FacilityCategory c = f.getCategory();
        OdorPreventionFacilityResponse facilityResponse =
            new OdorPreventionFacilityResponse(idx, f.getName(),
                    f.getBasePrice(), f.getPriceByWind());
        result.addResult_Facility(new Result_FacilityResponse(facilityResponse));
    }
	//ResultResponse 객체를 초기화하는 역할
    private void initResult(ResultResponse resultResponse) {
        resultResponse.setResult_Odors(new ArrayList<Result_OdorResponse>());
        resultResponse.setResult_Interferences(new ArrayList<Result_InterferenceResponse>());
        resultResponse.setResult_Additionals(new ArrayList<Result_AdditionalResponse>());
        resultResponse.setResult_Facilities(new ArrayList<Result_FacilityResponse>());
    }
	//악취 방지 시설 조합을 사용하여 주어진 입력 데이터를 처리하고 결과를 생성
	public void getResult(Response inputdata, FacilityCombination comb, ResultResponse resultResponse) {
		
        initResult(resultResponse);
		List<InputFactorDto.Target> inplist = new ArrayList<>();
		List<InterferenceFactorDto.Target> inflist = new ArrayList<>();
		
		// //이론적 희석배수 > 목표 희석배수이면 입력인자 리스트에 넣음
		// if(inputdata.getDilutionFactor() > inputdata.getTargetDilutionFactor()) {
		// 	for(Entry<Integer, List<InputFactorDto.Response>>  entry : inputdata.getInplist().entrySet()) {
		// 		for(int i = 0; i < entry.getValue().size(); i++) {
		// 			InputFactorDto.Response tmp = entry.getValue().get(i);
		// 			if(tmp.getValue() > 0) {
		// 				InputFactorDto.Target inp = new InputFactorDto.Target();
		// 				inp.setIdx(tmp.getIdx()); inp.setValue(tmp.getValue()); inp.setThreshold(tmp.getThreshold());
		// 				inp.setWaterSolubility(tmp.isWaterSolubility());
		// 				inplist.add(inp);
		// 			}
		// 		}
		// 	}
		// }
			//이론적 희석배수 > 목표 희석배수이면 입력인자 리스트에 넣음
				for(Entry<Integer, List<InputFactorDto.Response>>  entry : inputdata.getInplist().entrySet()) {
					for(int i = 0; i < entry.getValue().size(); i++) {
						InputFactorDto.Response tmp = entry.getValue().get(i);
						if(tmp.getValue() > 0) {
							InputFactorDto.Target inp = new InputFactorDto.Target();
							inp.setIdx(tmp.getIdx()); inp.setValue(tmp.getValue()); inp.setThreshold(tmp.getThreshold());
							inp.setWaterSolubility(tmp.isWaterSolubility());
							inplist.add(inp);
						}
					}
				}
			

		// //방해인자 값 > 목표값이면 방해인자 리스트에 넣음
		// for(int i = 0; i < inputdata.getInflist().size(); i++) {
		// 	InterferenceFactorDto.Response tmp = inputdata.getInflist().get(i);
		// 	if(tmp.getValue() > tmp.getTargetValue()) {
		// 		InterferenceFactorDto.Target inf = new InterferenceFactorDto.Target();
		// 		inf.setIdx(tmp.getIdx()); inf.setValue(tmp.getValue()); inf.setTargetValue(tmp.getTargetValue());
		// 		inflist.add(inf);
		// 	}
		// }
				//방해인자 값 > 목표값이면 방해인자 리스트에 넣음
				for(int i = 0; i < inputdata.getInflist().size(); i++) {
					InterferenceFactorDto.Response tmp = inputdata.getInflist().get(i);
					if (tmp.getValue() > 0) {
						InterferenceFactorDto.Target inf = new InterferenceFactorDto.Target();
						inf.setIdx(tmp.getIdx()); inf.setValue(tmp.getValue()); inf.setTargetValue(tmp.getTargetValue());
						inflist.add(inf);
					}
				}
				
				
		
		double expectedDF = inputdata.getDilutionFactor();
		  // 시설 조합을 통해 처리
		for(int i = 0; i < comb.getCombination().size(); i++) {
			CombinationResponse com = comb.getCombination().get(i);
			float efficiency = 0;
			int used = 0;
			
			if(com.getCategory().equals("탄소흡착시설")) {
				for(InputFactorDto.Target inp: inplist) {
					if(odorProofSystemMapper.isCO2(inp.getIdx()) && inp.getValue() > 0) {
						double dfSubtract = inp.getValue()*(0.9);
						inp.setValue(inp.getValue()*(0.1));
						expectedDF = expectedDF - dfSubtract;
						break;
					}
				}
			}
			else if(com.getCategory().equals("방지시설")) {
				/* count facility used number */
				for(int j = 0; j < comb.getCombination().size() && j <= i; j++) {
					CombinationResponse tmp = comb.getCombination().get(j);
					if(tmp.getFac_idx() == com.getFac_idx()) used++;
				}
				List<FacilityEfficiency.InpEff> efficiencies = odorProofSystemMapper.selectInpEff(com.getFac_idx());
				FacilityEfficiency.InpEff eff = efficiencies.get(0);
				for(InputFactorDto.Target inp: inplist) {
					//if(eff.getTarget() == null) //전부
					if(efficiencies.size() == 1 && eff.getTarget().equals(1) && !inp.isWaterSolubility()) continue; //수용성
					if(efficiencies.size() == 1 && eff.getTarget().equals(0) && inp.isWaterSolubility()) continue; //비수용성
					//get efficiency depending on 'used'
					if(used == 0) efficiency = eff.getEfficiency1();
					else if(used == 1) efficiency = eff.getEfficiency2();
					else if(used >= 2) efficiency = eff.getEfficiency3();
					
					double dfSubtract = inp.getValue()*efficiency/inp.getThreshold();
					inp.setValue(inp.getValue()*(1-efficiency));
					//update dilutionfactor 
					expectedDF = expectedDF - dfSubtract;
				}
			}
			else {
				/* count facility used number */
				for(CombinationResponse tmp: comb.getCombination()) {
					if(tmp.getFac_idx() == com.getFac_idx()) used++;
				}
				List<FacilityEfficiency.InfEff> effList = odorProofSystemMapper.selectInfEff(com.getFac_idx());
				//run
				for(InfEff eff: effList) {
					for(InterferenceFactorDto.Target inf: inflist) {
						if(inf.getIdx() == eff.getInf_idx()) {
							//get efficiency depending on 'used'
							if(used == 0) efficiency = eff.getEfficiency1();
							else if(used == 1) efficiency = eff.getEfficiency2();
							else if(used >= 2) efficiency = eff.getEfficiency3();
							inf.setValue(inf.getValue()*(1-efficiency));
						}
					}
				}
			}
		}
		
		for(InputFactorDto.Target inp:inplist) {
			double value = 0;
			int catg = odorProofSystemMapper.selectInpCatg(inp.getIdx());
			for(int k = 0; k < inputdata.getInplist().get(catg-1).size(); k++) {
				if(inputdata.getInplist().get(catg-1).get(k).getIdx() == inp.getIdx()) {
					value = inputdata.getInplist().get(catg-1).get(k).getValue();
					break;
				}
			}
			// Parameters: ResultResponse, idx, value, expectedValue
	        this.addOdor(resultResponse, inp.getIdx(), value, inp.getValue());  
		}
		
		for(InterferenceFactorDto.Target inf:inflist) {
			double value = 0;
			for(int k = 0; k < inputdata.getInflist().size(); k++) {
				if(inf.getIdx() == inputdata.getInflist().get(k).getIdx()) {
					value = inputdata.getInflist().get(k).getValue();
					break;
				}
			}
			// Parameters: ResultResponse, idx, value, targetValue, expectedValue
	        this.addInterference(resultResponse, inf.getIdx(), value, inf.getTargetValue(), inf.getValue());  
		}
		
		for(AdditionalFeatureDto.Response add: inputdata.getAddlist()) {
			// Parameters: ResultResponse, idx, available
	        this.addAdditional(resultResponse, add.getIdx(), add.isAvailable());
		}
		
		for(CombinationResponse com: comb.getCombination()) {
			// Parameters: ResultResponse, idx
	        this.addFacility(resultResponse, com.getFac_idx());
		}

        resultResponse.setTheoreticalDillutionFactor(inputdata.getDilutionFactor());
        resultResponse.setTargetDillutionFactor(inputdata.getTargetDilutionFactor());
        resultResponse.setExpectedDillutionFactor(expectedDF);
        resultResponse.setWind(inputdata.getWind());
	}

}
