package com.OdorPreventSystem.domain.solution.module.predict.dto;

import lombok.Data;

public class FacilityEfficiency {
	// 시설 효율
	@Data
	public static class InfEff { //방해인자 효율
		private int inf_idx;
		private float efficiency1;
		private float efficiency2;
		private float efficiency3;
	}
	
	@Data
	public static class InpEff { // 입력인지 효율
		private Integer target; //목표 인자
		private float efficiency1;
		private float efficiency2;
		private float efficiency3;
	}
}
