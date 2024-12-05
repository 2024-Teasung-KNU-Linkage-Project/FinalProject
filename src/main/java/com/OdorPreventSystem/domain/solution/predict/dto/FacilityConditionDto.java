package com.OdorPreventSystem.domain.solution.predict.dto;

// import java.util.List;

import lombok.Data;

public class FacilityConditionDto {
	//시설조건
	@Data
	public static class RefCondition{ //참조조건
		private int var1; 	//방해인자 번호 혹은 입력인자 번호
		private String op;	//연산자
		private double var2;	//상수
	}
	
	@Data
	public static class TargetFactor{ //목표인자
		private int inp_idx; 
	}
}
