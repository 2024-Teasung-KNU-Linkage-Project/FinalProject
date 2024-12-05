package com.OdorPreventSystem.domain.solution.inputFactor.dto;


import java.util.List;
import java.util.Map;

import lombok.Data;

//사용자로부터 입력받은 데이터를 담고 있는 전송 객체
public class UserInputDataDto {
	@Data
	public static class Response{
		private Map<Integer, List<InputFactorDto.Response>> inplist;// 입력 인자 리스트를 저장하는 맵 (키는 인덱스, 값은 InputFactorDto.Response 객체 리스트)
		private List<InterferenceFactorDto.Response> inflist;//방해인자 리스트
		private List<AdditionalFeatureDto.Response> addlist; //추가설비 리스트
		private int dilutionFactor = 0; //전체 이론적 희석베수
		private int targetDilutionFactor = 0; //목표희석배수
		private double wind = 0.00; //풍량
		private List<String> catelist; //카테고리 리스트
	}
	
	@Data
	public static class Request{
		private List<List<InputFactorDto.Request>> inplist; //입력인자 요청리스트
		private List<InterferenceFactorDto.Request> inflist; //방해인자 요청리스트
		private List<AdditionalFeatureDto.Request> addlist; // 추가설비 요청리스트
		private int targetDilutionFactor; //목표희석배수
		private double wind; //풍량
		
		
	}
}