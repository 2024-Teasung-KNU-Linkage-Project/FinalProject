package com.OdorPreventSystem.domain.solution.result;

import com.OdorPreventSystem.domain.solution.result.service.ResultInitService;
import com.OdorPreventSystem.domain.solution.result.service.ResultStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.OdorPreventSystem.domain.solution.result.dto.ResultRequest;
import com.OdorPreventSystem.domain.solution.result.dto.ResultResponse;

/*
 * ResultTabController:
 *     결과 탭 컨트롤러
 */
@Controller
@RequestMapping("/soms/page5/result")
@SessionAttributes("resultResponse")
public class ResultTabController {

 
    @Autowired
    ResultInitService resultInitService;

    @Autowired
    ResultStoreService resultStoreService;

    //모든 요청 전에 호출, 결과 모델 추가
    @ModelAttribute
    public void addCategoryToModel(Model model, ResultResponse resultResponse) {
        model.addAttribute("odorCategories", resultInitService //결과에 냄새 카테고리를 가져와 모델에 추가
                .getOdorCategories(resultResponse.getResult_Odors()));
        model.addAttribute("facilityCategories", resultInitService //결과에 시설 카테고리 가져와 모델 추가
                .getFacilityCategories(resultResponse.getResult_Facilities()));
    }

    // "resultResponse"라는 이름으로 초기화된 ResultResponse 객체를 반환
    @ModelAttribute(name = "resultResponse")
    public ResultResponse resultResponse() {
        return new ResultResponse();
    }

    // "resultRequest"라는 이름으로 초기화된 ResultRequest 객체를 반환
    @ModelAttribute(name = "resultRequest")
    public ResultRequest resultRequest() {
        return new ResultRequest();
    }

    @GetMapping
    public String showResultTab() {
        return "solution/result"; //result 뷰 반환=> 결과 표시
    }

    /*
     * storeInDatabase:
     *     시작조건: "DB에 저장하기" 버튼 클릭
     *     입력과 결과를 데이터베이스에 저장
     */
    @PostMapping( params="storeIn=database")
    public String storeInDatabase(@SessionAttribute ResultResponse resultResponse,
            ResultRequest resultRequest) {   
        String requestName = resultRequest.getName(); 

     //결과요청의 이름이 빈 문자열이 아닌 경우에만 DB 저장 
        if (requestName != null && !requestName.trim().isEmpty()) {
               resultStoreService.storeInDatabase(resultResponse, resultRequest);
        } 
        return "solution/result"; //result로 리다이렉트
    }

    
    // /*
    //  * storeInFile:
    //  *     시작조건: "파일로 내보내기" 버튼 클릭
    //  *     입력과 결과를 CSV 파일에 저장
    //  */
    @PostMapping(params = "storeIn=file")
    public ResponseEntity<String> storeInFile(
            @SessionAttribute ResultResponse resultResponse,
            @RequestParam("fileName") String fileName) {

        // 파일 이름이 없으면 현재 화면을 유지 (아무 작업도 하지 않음)
        if (fileName == null || fileName.trim().isEmpty()) {
            // 빈 응답 반환, 화면을 유지하기 위해 상태 코드 204 No Content 사용 가능
            return ResponseEntity.noContent().build();
        }//_.csv 파일 받는거 방지

        // HTTP 응답 헤더를 설정
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "text/csv"); // 콘텐츠 타입을 CSV로 설정
        header.add("Content-Disposition", "attachment;filename=\"" + fileName + "\".csv"); // 파일 다운로드 시 파일 이름 설정

        // 생성된 CSV 콘텐츠와 헤더를 포함한 ResponseEntity를 반환
        return new ResponseEntity<>(
                resultStoreService.createContent(resultResponse), header, HttpStatus.CREATED);
    }
}