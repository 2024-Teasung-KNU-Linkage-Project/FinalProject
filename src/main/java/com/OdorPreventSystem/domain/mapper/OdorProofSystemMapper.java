package com.OdorPreventSystem.domain.mapper;

import java.util.List;

import com.OdorPreventSystem.domain.complaint.dto.complaintDTO;
import com.OdorPreventSystem.domain.complaint.dto.complaintLocationDateDTO;
import com.OdorPreventSystem.domain.complaint.dto.odorTypeDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.carDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.chemicalDataDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.placeDataDTO;
import com.OdorPreventSystem.domain.trackingByCar.vo.location;
import org.apache.ibatis.annotations.Mapper;

import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityConditionDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityConditionDto.RefCondition;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityConditionDto.TargetFactor;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency.InfEff;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency.InpEff;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.AdditionalFeatureDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InterferenceFactorDto;
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

	// 사업장 정보 mapper
	public List<com.OdorPreventSystem.domain.complaint.dto.placeDataDTO> getPlaceComplaint() throws Exception;

	public List<com.OdorPreventSystem.domain.complaint.dto.chemicalDataDTO> getPlaceCsvContentById(String companyIndex) throws Exception;

	// 민원 정보 mapper
	public List<complaintDTO> getComplaint(int complaintId) throws Exception;

	// 민원 위치 정보 mapper
	public List<complaintLocationDateDTO> getComplaintLocationDate() throws Exception;


	// 민원 검색 시 mapper
	public List<complaintDTO> getComplaintsByDateRangeAndOdor(
			@Param("startDateTime") String startDateTime,
			@Param("endDateTime") String endDateTime,
			@Param("odorName") String odorName,
			@Param("odorIntensity") String odorIntensity) throws Exception;

	public List<complaintDTO> getComplaintsByDateRangeAndOdorByOdorId(
			@Param("startDateTime") String startDateTime,
			@Param("endDateTime") String endDateTime,
			@Param("odorId") Long odorId,
			@Param("odorIntensity") String odorIntensity) throws Exception;


	//민원 저장 mapper
	public void insertComplaint(complaintDTO complaint);

	// 악취 종류 이름 mapper
	public List<odorTypeDTO> getOdorType() throws Exception;

	public String getOdorNameById(@Param("odorId") Integer odorId) throws Exception;

	public Integer getOdorIdByName(@Param("odorName") String odorName) throws  Exception;



	// 지도 중심 좌표를 기준으로 특정 반경 내 "민원 정보들"을 가져오는 mapper>>지역별로 들고오는 민원 기능
	// public List<complaintDTO> getComplaintwithRadius(
	// @Param("centerLngtitude") double centerLngitude,
	// @Param("centerLatitude") double centerLatitude,
	// @Param("raidus") double radius
	// ) throws Exception;

}
