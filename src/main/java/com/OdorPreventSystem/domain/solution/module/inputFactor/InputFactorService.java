package com.OdorPreventSystem.domain.solution.module.inputFactor;

import java.util.List;

import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.AdditionalFeatureDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto.DilutionFactorResponse;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InterferenceFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto.Request;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto.Response;



public interface InputFactorService {

	List<InputFactorDto.Entity> selectInpList();//입력인자 리스트 불러옴
	List<InterferenceFactorDto.Response> selectInfList(); //방해인자 리스트
	List<AdditionalFeatureDto.Response> selectAddList();//추가시설 리스트
	List<String> selectCategoryList(); //입력요소의 종류 리스트
	UserInputDataDto.Response initInputData(); //초기 입력 데이터 생성
	List<DilutionFactorResponse> calculateDilutionFactors(UserInputDataDto.Response inputdata); //희석배수 계산 
	void saveData(Request inputdatareq, Response inputdata);//데이터 저장
	void updateInputData(Response res);//업데이트
	
	UserInputDataDto.Response handleInputData(UserInputDataDto.Response res, String fileName, Long companyIndex) throws Exception;// 데이터 처리
	void resetInputData(UserInputDataDto.Response res); // 데이터 초기화

	 // 물질 값이 모두 0인지 확인하는 메서드 선언
	 boolean checkIfAllValuesAreZero(UserInputDataDto.Response inputData);

}
