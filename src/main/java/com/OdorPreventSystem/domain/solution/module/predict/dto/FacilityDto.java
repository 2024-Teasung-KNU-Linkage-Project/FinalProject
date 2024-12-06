package com.OdorPreventSystem.domain.solution.module.predict.dto;

import java.math.BigInteger;

// import com.prooffacilitysystem.dto.userinputdata.InputFactorDto.Target;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//시설데이터
public class FacilityDto {
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response{
		private int fac_idx;
		private String name;
		private boolean available; //시설가용성여부  => 사용가능한 시설만 방지시설에 뜸
		private boolean hidden; //시설 숨김 여부
	}
	
	@Data
	public static class Request{
		private boolean available;
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	public static class FindCombination implements Cloneable {
		private int fac_idx;
		private int category;
		
		public FindCombination clone() throws CloneNotSupportedException {
			return (FindCombination) super.clone();
		}
	}
	
	@Data
	public static class CombinationResponse{
		private int fac_idx;
		private String category;
		private String name;
		private String imagesrc; //시설이미지
		private BigInteger cost; //단가
	}
	
	
	@Data
	public static class Entity{
		private int fac_idx;
		private String name;
		private String imagesrc;
		private int priority; //우선순위
		private int requiredAdditionalFeature;
		private BigInteger costByWind;//풍량에 따른 비용
		private BigInteger costBase; //기본 비용
		private int category;
	}

}
