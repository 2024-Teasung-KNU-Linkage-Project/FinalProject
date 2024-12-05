package com.OdorPreventSystem.domain.mapper;

import java.util.List;

import com.OdorPreventSystem.domain.trackingByCar.dto.carDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.chemicalDataDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.placeDataDTO;
import com.OdorPreventSystem.domain.trackingByCar.vo.location;
import org.apache.ibatis.annotations.Mapper;

import com.OdorPreventSystem.domain.solution.predict.dto.FacilityConditionDto;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityConditionDto.RefCondition;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityConditionDto.TargetFactor;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityDto;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityEfficiency.InfEff;
import com.OdorPreventSystem.domain.solution.predict.dto.FacilityEfficiency.InpEff;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.AdditionalFeatureDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InterferenceFactorDto;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface OdorProofSystemMapper {

	List<InputFactorDto.Entity> selectInpList(); // 입력인자 리스트를 데이터베이스에서 조회하며 반환
	List<InterferenceFactorDto.Response> selectInfList(); // 방해인자리스트 반환
	List<AdditionalFeatureDto.Response> selectAddList(); // 추가설비 반환
	List<String> selectCategoryList(); //입력인자 종류 리스트 반환
	List<FacilityDto.Entity> selectFacilityList(); //시설리스트 반환
	
	String selectAddName(int idx); //추가설비 이름 번호로 찾기
	List<FacilityConditionDto.RefCondition> selectInfConditionByIdx(int idx); //방해인자 조건리스트 반환
	List<RefCondition> selectInpConditionByIdx(int idx); //입력인자 조건리스트 반환
	int selectInpCatg(int var1);// 변수 입력인자 카데고리 반환
	List<TargetFactor> selectTargetFactor(int idx); //목표인자리스트 반환
	List<Integer> selectUsableFacInf(int inf_idx); //사용 가능 시설 리스트 반환
	List<Integer> selectUsableFacWaterSoluble(); //수용성 리스트 반환
	List<Integer> selectUsableFacWaterUnsoluble();//비수용성 리스트 반환
	List<InfEff> selectInfEff(int fac_idx); //방해인자 효율 리스트 반환
	List<InpEff> selectInpEff(int fac_idx); //입력인자 효율 리스트 반환
	boolean isCO2(int idx); //입력인자 CO2인지 여부

	public chemicalDataDTO getMsv(String chemicalName) throws Exception;


	public List<placeDataDTO> getplace() throws Exception;

	public List<chemicalDataDTO> getCarCsvContent(String fileName) throws Exception;
	public List<chemicalDataDTO> getPlaceCsvContent(String fileName) throws Exception;
	List<chemicalDataDTO> getPlaceCsvContentByCompany(Long company) throws Exception;


	public List<carDTO> getCar(
			@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("selectCar") String selectCar
	) throws Exception;

	public List<location> getCarLocation(
			@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("selectCar") String selectCar
	) throws Exception;

	public List<carDTO> getRealtimeCar(
			@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("selectCar") String selectCar,
			@Param("lastQueryTime") String lastQueryTime
	)
			throws Exception;

	public List<location> getRealtimeCarLocation(
			@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("selectCar") String selectCar,
			@Param("lastQueryTime") String lastQueryTime
	)
			throws Exception;

	public String[] getGPSDate(String carCode) throws Exception;

	public String[] searchPlace(String name) throws Exception;

}
