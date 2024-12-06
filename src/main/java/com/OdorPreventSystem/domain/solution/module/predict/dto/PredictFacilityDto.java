package com.OdorPreventSystem.domain.solution.module.predict.dto;

import java.util.List;

import lombok.Data;
//예측설비 데이터
public class PredictFacilityDto {
	@Data
	public static class Response{
		private List<FacilityDto.Response> prefacilityList; //전처리시설 리스트
		private List<FacilityDto.Response> prooffacilityList; //방지시설 리스트
		private List<FacilityDto.Response> postfacilityList; //후처리시설 리스트
		private List<FacilityDto.Response> carbonfacilityList; //탄소흡착시설 리스트
	}
	
	@Data public static class Request{
		private List<FacilityDto.Request> prefacilityList;
		private List<FacilityDto.Request> prooffacilityList;
		private List<FacilityDto.Request> postfacilityList;
		private List<FacilityDto.Request> carbonfacilityList;
	}

}
