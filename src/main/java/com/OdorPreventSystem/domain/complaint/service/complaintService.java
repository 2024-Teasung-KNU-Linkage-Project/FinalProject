package com.OdorPreventSystem.domain.complaint.service;

import com.OdorPreventSystem.domain.complaint.dto.*;

import java.util.List;

public interface complaintService {
    // 사업장 정보 Get 로직
    public List<placeDataDTO> getPlace() throws Exception;

    public List<chemicalDataDTO> getPlaceCsvContent(String companyIndex) throws Exception;

    // 민원 정보 Get 로직
    public List<complaintDTO> getComplaint(int complaintId) throws Exception;

    // 민원 위치 정보 Get 로직
    public List<complaintLocationDateDTO> getComplaintLocationDate() throws Exception;

    // 검색 민원 정보 Get 로직
    public List<complaintDTO> getComplaintsByDateRangeAndOdor(String startDateTime, String endDateTime, String odorName,
            String odorIntensity) throws Exception;

    public List<complaintDTO> getComplaintsByDateRangeAndOdorByOdorId(String startDateTime, String endDateTime, Long odorId,
                                                              String odorIntensity) throws Exception;

    // 악취 종류 이름 Get 로직
    public List<odorTypeDTO> getOdorType() throws Exception;

    //민원 저장 로직
    public void saveComplaint(complaintDTO complaint) throws Exception;

    public Integer getOdorIdByName(String odorName) throws Exception;

    // 지도 중심좌표기준으로 특정 반경 내 민원을 들고 오는 service **필요하면 주석 해제
    // public List<complaintDTO> getComplaintwithRadius(
    // double centerLngitude, double centerLatitude, double radius) throws
    // Exception;

}