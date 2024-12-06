package com.OdorPreventSystem.domain.solution.module.predict;

import java.util.List;

import com.OdorPreventSystem.domain.solution.module.result.service.ResultPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityCombination;
import com.OdorPreventSystem.domain.solution.module.predict.dto.PredictFacilityDto;
import com.OdorPreventSystem.domain.solution.module.result.dto.ResultResponse;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto;


@Controller
@RequestMapping("/soms/page5/predict")
@SessionAttributes({"resultResponse", "facility"}) //"resultResponse"와 "facility"라는 이름의 모델 속성을 세션에 저장하도록 지정
public class PredictTabController {


	@Autowired
    FacilityPredictService facilityPredictService;

    @Autowired
    ResultPassService resultPassService;


    //"resultResponse"라는 이름의 모델 속성을 초기화하는 메서드
    @ModelAttribute(name = "resultResponse")
    public ResultResponse result() {
        return new ResultResponse(); // 새로운 ResultResponse 객체를 반환
    }
     // "facility"라는 이름의 모델 속성을 초기화하는 메서드
    @ModelAttribute(name = "facility")
    public PredictFacilityDto.Response getFacility(){
       // FacilityPredictService를 사용하여 PredictFacilityDto.Response 객체를 초기화
    	return facilityPredictService.initPredictDto();
    }
    
    //방지시설 예측//
    @GetMapping
    public String showPredictTab(Model model, @SessionAttribute(name = "inputdata", required = false) UserInputDataDto.Response inputdata,
    											@SessionAttribute(name = "facility", required = false) PredictFacilityDto.Response facility,
                                                  RedirectAttributes redirectAttributes) {



    	if(facility == null) facility = getFacility();
    	facilityPredictService.updateFacility(); //설정 탭에서 수정한 내용 반영

        System.out.println(inputdata.toString() + facility.toString());

        //시설 조합을 찾고 모델에 추가
    	if(inputdata != null) {
    		List<FacilityCombination> comList = facilityPredictService.findCombinations(inputdata, facility);
    		model.addAttribute("comList", comList);
    		model.addAttribute("facility", facility);


            // UserInputDataDto.Response 객체를 모델에 추가
            model.addAttribute("inputdata", inputdata);
    	} else{
            redirectAttributes.addFlashAttribute("errorMessage", "입력 데이터가 없습니다. 입력 페이지로 이동합니다.");
            return "redirect:/soms/page5/input";
        }
        // "predict"라는 뷰 이름을 반환하여 해당 뷰를 렌더링
        return "solution/predict";
    }
    
    //결과 조회//
    @RequestMapping(params = "result")
    public String processResult(@SessionAttribute(name="resultResponse") ResultResponse resultResponse,
    								@SessionAttribute(name = "inputdata", required = false) UserInputDataDto.Response inputdata,
    								@RequestParam int result) {
        // 선택된 결과에 해당하는 FacilityCombination을 가져옴
    	FacilityCombination comb = facilityPredictService.getCombination(result - 1);
         // ResultPassService를 사용하여 결과를 처리
    	resultPassService.getResult(inputdata, comb, resultResponse);
    	
        return "redirect:/soms/page5/result";
    }
    
    //시설 이미지 표시//
    @RequestMapping(params = "i")
    @ResponseBody
    public List<String> getImages(@RequestParam int i){
    	//System.out.println("image requested" + i);

        // 요청된 인덱스에 해당하는 시설 이미지를 가져옴
    	List<String> images = facilityPredictService.getImages(i-1);
    	return images;
    }
    
    //전처리시설 체크박스 처리//
    @RequestMapping(params = "pre")
    public String processCheckPre(@SessionAttribute(name = "facility") PredictFacilityDto.Response facility, PredictFacilityDto.Request facilityreq) {
    	// 전처리시설 데이터를 저장
        facilityPredictService.saveData(facility, facilityreq);
    	return "redirect:/soms/page5/predict";
    }
    
    //방지시설 체크박스 처리//
    @RequestMapping(params = "proof")
    public String processCheckProof(@SessionAttribute(name = "facility") PredictFacilityDto.Response facility, PredictFacilityDto.Request facilityreq) {
    	// 전처리시설 데이터를 저장
        facilityPredictService.saveData(facility, facilityreq);
    	return "redirect:/soms/page5/predict";
    }
    
    //후처리시설 체크박스 처리//
    @RequestMapping(params = "post")
    public String processCheckPost(@SessionAttribute(name = "facility") PredictFacilityDto.Response facility, PredictFacilityDto.Request facilityreq) {
    	// 후처리시설 데이터를 저장
        facilityPredictService.saveData(facility, facilityreq);
    	return "redirect:/soms/page5/predict";
    }
    
    //탄소흡착시설 체크박스 처리//
    @RequestMapping(params = "carb")
    public String processCheckCarb(@SessionAttribute(name = "facility") PredictFacilityDto.Response facility, PredictFacilityDto.Request facilityreq) {
    	 // 탄소흡착시설 데이터를 저장
        facilityPredictService.saveData(facility, facilityreq);
    	return "redirect:/soms/page5/predict";
    }

}
