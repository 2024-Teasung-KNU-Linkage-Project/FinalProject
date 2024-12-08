package com.OdorPreventSystem.domain.complaint.controller;

import com.OdorPreventSystem.domain.complaint.dto.complaintDTO;
import com.OdorPreventSystem.domain.complaint.service.complaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/complaint")
public class complaintRestController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private complaintService service;

    // 사업장 정보 GET
    @GetMapping("/places")
    public Map<String, Object> getplace() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", service.getPlace());
        return map;
    }

    // 민원 전체 요소 GET
    @GetMapping("/complaintdata")
    public Map<String, Object> getcomplaint(@RequestParam(value = "complaintId") int complaintId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", service.getComplaint(complaintId));
        System.out.println(complaintId);
        return map;
    }

    // 민원 위치 정보 요소 GET
    @GetMapping("/complaintLocationDate")
    public Map<String, Object> getcomplaintLocationDate() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", service.getComplaintLocationDate());
        for(Map.Entry<String, Object> entry : map.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        return map;
    }

    // 사업장의 화학 정보 GET
    @GetMapping("/complaintPlaceCsvContent")
    public Map<String, Object> getPlaceCsvContent(
            @RequestParam(value = "company_id") String companyIdex) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", service.getPlaceCsvContent(companyIdex));
        return map;
    }

    // 민원 검색 시 정보 GET
    @GetMapping("/searchComplaintStartEnd")
    public Map<String, Object> searchComplaintsStartEnd(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "odorName", required = false) String odorName,
            @RequestParam(value = "odorId", required = false) String odorId,
            @RequestParam(value = "odorIntensity", required = false) String odorIntensity) throws Exception {

        Map<String, Object> map = new HashMap<>();

        String startDateTime = null;
        String endDateTime = null;

        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = startDate + " 00:00:00";
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = endDate + " 23:59:59";
        }


        long begin1, end1, begin2, end2;


//        begin1 = System.currentTimeMillis();
//        map.put("list", service.getComplaintsByDateRangeAndOdor(startDateTime, endDateTime, odorName, odorIntensity));
//        end1 = System.currentTimeMillis();
//
//        System.out.println("이름으로 비교하는 버전 : " + (end1-begin1) + " 밀리초 소요");

        begin2 = System.currentTimeMillis();
        map.put("list", service.getComplaintsByDateRangeAndOdorByOdorId(startDateTime, endDateTime, Long.valueOf(odorId), odorIntensity));
        end2 = System.currentTimeMillis();

        System.out.println("Id 으로 비교하는 버전 : " + (end2-begin2) + " 밀리초 소요");

        return map;
    }

    // 악취 종류 이름 GET -->검색창 악취 종류요소에 표시하기 위함
    @GetMapping("/odorNameAll")
    public Map<String, Object> getodorNameALL() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", service.getOdorType());
        return map;
    }


    @PostMapping("/submit")
    public Map<String, Object> submitComplaint(@RequestBody complaintDTO complaint) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 데이터 검증
            if (complaint.getDate() == null || complaint.getLatitude() == null ||
                    complaint.getLongitude() == null || complaint.getOdorName() == null ||
                    complaint.getOdorIntensity() == null) {
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("message", "필수 데이터가 누락되었습니다.");
                return response; // 에러 응답
            }
            Integer odorId = service.getOdorIdByName(complaint.getOdorName());
            complaint.setOdor(odorId);
            // 민원 데이터 저장 서비스 호출
            service.saveComplaint(complaint);
            response.put("status", "success");
            response.put("message", "민원이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            log.error("민원 접수 오류", e);
            response.put("status", "error");
            response.put("message", "민원 접수 중 오류 발생");
        }
        return response;
    }
}
