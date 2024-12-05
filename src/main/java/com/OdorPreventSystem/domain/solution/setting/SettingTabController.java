package com.OdorPreventSystem.domain.solution.setting;

import com.OdorPreventSystem.domain.solution.setting.service.SettingInitService;
import com.OdorPreventSystem.domain.solution.setting.service.SettingUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.OdorPreventSystem.domain.solution.setting.dto.SettingRequest;

/*
 * SettingTabController
 *     설정 탭 컨트롤러
 */
@Controller
@RequestMapping("/soms/page5/setting")
public class SettingTabController {

    @Autowired
    SettingInitService settingInitService;

    @Autowired
    SettingUpdateService settingUpdateService;
 
    
    // 모든 요청 전에 호출되며, 설정 요소를 모델에 추가
    @ModelAttribute
    public void addSettingFactorToModel(Model model) {
        model.addAttribute("odorCategories", settingInitService.getOdorCategories()); // 악취 카테고리를 모델에 추가
        model.addAttribute("waterSolubilities", settingInitService.getWaterSolubilities());  // 수용성 카테고리를 모델에 추가
        model.addAttribute("facilityCategories", settingInitService.getFacilityCategories()); // 시설 카테고리를 모델에 추가
        model.addAttribute("settingRequest", settingInitService.initSettingRequest()); // 데이터 베이스에서 불러온 정보 모델에 추가
    }

    @GetMapping
    public String showSettingTab() {
         // "setting"이라는 뷰 이름을 반환 =>설정 탭을 표시
        return "solution/setting";
    }

    /*
     * updateOdor:
     *     시작조건: 악취기여물질 섹션의 저장 버튼 클릭
     *     악취기여물질 설정을 데이터베이스에 저장
     */
    @PostMapping(params="update=odor")
    public String updateOdor(SettingRequest settingRequest) {
          // 설정 요청 객체에서 악취 요청을 가져와 업데이트 서비스를 호출
        settingUpdateService.updateOdor(settingRequest.getOdorRequests());
        return "redirect:/soms/page5/setting"; //작업완료후 경로로 리다이렉트
    }

    /*
     * updateFacility:
     *     시작조건: 악취방지시설 섹션의 저장 버튼 클릭
     *     악취방지시설 설정을 데이터베이스에 저장
     */
    @PostMapping(params="update=facility")
    public String updateFacility(SettingRequest settingRequest) {
         // 설정 요청 객체에서 시설 요청과 효율 요청을 가져와 업데이트 서비스를 호출
        settingUpdateService.updateFacility(settingRequest.getFacilityRequests(),
                settingRequest.getEfficiencyRequests());
        return "redirect:/soms/page5/setting"; 
    }

}
