package com.OdorPreventSystem.domain.solution.inputFactor.dto;

import lombok.Data;

//추가설비//
 // 'Response' 클래스는 서버에서 클라이언트로 보낼 데이터 구조를 정의
  // Lombok 라이브러리의 어노테이션으로, 자동으로 getter, setter, toString, equals, hashCode 메서드를 생성
public class AdditionalFeatureDto {
	@Data
	public static class Response{
		private int idx; // 추가 설비의 고유 식별자
		private String name; // 추가 설비의 이름
		private boolean available; // 추가 설비가 사용 가능한지 여부
	}
	
	@Data
	public static class Request{
		private boolean available;
	}
}
