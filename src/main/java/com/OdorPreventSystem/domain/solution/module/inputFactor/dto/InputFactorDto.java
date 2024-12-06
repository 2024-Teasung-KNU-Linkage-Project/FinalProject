package com.OdorPreventSystem.domain.solution.module.inputFactor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
//import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.Setter;

//입력인자 데이터전송객체
public class InputFactorDto {
	@Data
	@Builder  // Builder 패턴을 사용할 수 있게 함
	@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 생성
	public static class Response{
		private int idx; //인자 고유 식별자
		private String name; //성분
		private double value; // 농도
		private double threshold; // 최소감지값
		private boolean waterSolubility; //수용성
	}
	
	@Data
	@Builder
	@NoArgsConstructor // 기본 생성자를 생성
	@AllArgsConstructor
	
	//이론적 희석배수, 기여율 정보를 담은 dto
	public static class DilutionFactorResponse{ 
		
		private String name; // 이름
		private int dilutionFactor; //이론적 희석배수
		private double contRate; //기여율
	}
	
	@Data
	public static class Request{
		private double value;
	}
	
	@Data
	 // Cloneable 인터페이스를 구현하여 객체 복제가 가능
	public static class Target implements Cloneable{
		private int idx;
		private double value;
		private double threshold;
		private boolean waterSolubility;
		 // Cloneable 인터페이스의 clone 메서드를 사용하여 객체를 복제
		public Target clone() throws CloneNotSupportedException {
			return (Target) super.clone();
		}
	}
	
	@Data
	public static class Entity{
		private int idx;
		private String name;
		private double value;
		private double threshold;
		private boolean waterSolubility;
		private String category; //인자 카테고리
	}
}
