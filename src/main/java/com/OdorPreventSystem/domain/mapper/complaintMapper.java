package com.OdorPreventSystem.domain.mapper;

import com.OdorPreventSystem.domain.complaint.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface complaintMapper {

    // 사업장 정보 mapper
    public List<placeDataDTO> getPlaceComplaint() throws Exception;

    public List<chemicalDataDTO> getPlaceCsvContentById(String companyIndex) throws Exception;

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

    // 악취 종류 이름 mapper
    public List<odorTypeDTO> getOdorType() throws Exception;

    public String getOdorNameById(@Param("odorId") Integer odorId) throws Exception;

    // 지도 중심 좌표를 기준으로 특정 반경 내 "민원 정보들"을 가져오는 mapper>>지역별로 들고오는 민원 기능
    // public List<complaintDTO> getComplaintwithRadius(
    // @Param("centerLngtitude") double centerLngitude,
    // @Param("centerLatitude") double centerLatitude,
    // @Param("raidus") double radius
    // ) throws Exception;

}
