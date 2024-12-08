package com.OdorPreventSystem.domain.complaint.service;

import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;
import com.OdorPreventSystem.domain.complaint.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class complaintServiceImpl implements complaintService {

    @Autowired
    private OdorProofSystemMapper mapper;

    // 사업장 정보 GET
    @Override
    public List<placeDataDTO> getPlace() throws Exception {
        return mapper.getPlaceComplaint();
    }

    // 사업장 화학 정보 GET
    @Override
    public List<chemicalDataDTO> getPlaceCsvContent(String companyIndex) throws Exception {
        return mapper.getPlaceCsvContentById(companyIndex);
    }

    // 민원 정보 GET
    @Override
    public List<complaintDTO> getComplaint(int complaintId) throws Exception {
        return mapper.getComplaint(complaintId);
    }

    // 민원 위치 정보 GET
    @Override
    public List<complaintLocationDateDTO> getComplaintLocationDate() throws Exception {
        return mapper.getComplaintLocationDate();
    }

    // 민원 검색 시 해당 민원 정보 GET
    @Override
    public List<complaintDTO> getComplaintsByDateRangeAndOdor(
            String startDateTime,
            String endDateTime,
            String odorName,
            String odorIntensity) throws Exception {
        return mapper.getComplaintsByDateRangeAndOdor(startDateTime, endDateTime, odorName, odorIntensity);
    }

    // 민원 검색 시 해당 민원 정보 GET
    @Override
    public List<complaintDTO> getComplaintsByDateRangeAndOdorByOdorId(
            String startDateTime,
            String endDateTime,
            Long odorId,
            String odorIntensity) throws Exception {
        return mapper.getComplaintsByDateRangeAndOdorByOdorId(startDateTime, endDateTime, odorId, odorIntensity);
    }

    // 악취 종류 이름 GET
    @Override
    public List<odorTypeDTO> getOdorType() throws Exception {
        return mapper.getOdorType();
    }

    @Override
    public void saveComplaint(complaintDTO complaint) throws Exception {
        // 민원 데이터를 데이터베이스에 저장
        mapper.insertComplaint(complaint);
    }

    @Override
    public Integer getOdorIdByName(String odorName) throws Exception {
        return mapper.getOdorIdByName(odorName);
    }

    // @Override
    // public List<complaintDTO> getComplaintwithRadius(double centerLngitude,
    // double centerLatitude, double radius)
    // throws Exception {
    // return mapper.getComplaintwithRadius(centerLngitude, centerLatitude, radius);
    // }

}
