package com.OdorPreventSystem.domain.solution.module.inputFactor.dto;

// import com.prooffacilitysystem.dto.userinputdata.InputFactorDto.Target;

import lombok.Data;

//방해인자 관련 데이더를 담고 있는 전송 객체
public class InterferenceFactorDto {
	@Data
	public static class Response{
		private int idx;
		private String name;
		private double value; //실측값
		private double targetValue; //목표값
	}
	
	@Data
	public static class Request{
		private double value;
		private double targetValue;
	}
	
	@Data
	public static class Target implements Cloneable {
		private int idx;
		private double value;
		private double targetValue;
		
		public Target clone() throws CloneNotSupportedException { //객체클론
			return (Target) super.clone();
		}
	}
}
