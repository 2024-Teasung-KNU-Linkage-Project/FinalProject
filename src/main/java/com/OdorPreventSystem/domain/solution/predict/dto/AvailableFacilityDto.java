package com.OdorPreventSystem.domain.solution.predict.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
 //사용가능한 설비정보
public class AvailableFacilityDto implements Comparable<AvailableFacilityDto>, Cloneable {
	private int fac_idx; 
	private int priority; //시설우선순위
	private boolean available; //체크박스로 선택한 값 아님, 조합 찾는 알고리즘에서 사용
	
	@Override //매서드 재정의
	public int compareTo(AvailableFacilityDto o) {
		//우선순위비교
		if(o.priority < priority) return 1;
		else if(o.priority > priority) return -1;
		return 0;
	}
	
	public AvailableFacilityDto clone() throws CloneNotSupportedException {
		return (AvailableFacilityDto) super.clone(); //객체클론
	}

}
