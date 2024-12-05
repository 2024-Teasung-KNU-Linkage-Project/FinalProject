package com.OdorPreventSystem.domain.solution.predict;

import java.util.List;

import com.OdorPreventSystem.domain.solution.predict.dto.FacilityCombination;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityDto;
import com.OdorPreventSystem.domain.solution.predict.dto.PredictFacilityDto;
import com.OdorPreventSystem.domain.solution.predict.dto.PredictFacilityDto.Request;
import com.OdorPreventSystem.domain.solution.predict.dto.PredictFacilityDto.Response;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.UserInputDataDto;

public interface FacilityPredictService {
	List<FacilityDto.Entity> selectFacilityList(); //시설리스트 불러옴
	PredictFacilityDto.Response initPredictDto(); // 예측설비 응답객체 초기화
	List<FacilityCombination> findCombinations(UserInputDataDto.Response inputdata, PredictFacilityDto.Response facility);//입력 데이터 기반 시설 조합 찾기 
	List<String> getImages(int index); //이미지 가져옴
	FacilityCombination getCombination(int i); //조합 객체 반환
	void saveData(Response facility, Request facilityreq);//저장
	void updateFacility(); //업데이트

}
