package com.OdorPreventSystem.domain.solution.module.predict.dto;

import java.math.BigInteger;
import java.util.List;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto;
import lombok.Data;

//방지시설 선택 부분의 희석배수결과와 단가총합부분에 들어갈 데이터
@Data
public class FacilityCombination {
	private List<FacilityDto.CombinationResponse> combination;
	private int dilutionFactor; //희석배수결과
	private BigInteger costTotal; //단가 총합
}
