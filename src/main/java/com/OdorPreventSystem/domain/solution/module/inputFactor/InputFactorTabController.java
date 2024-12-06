package com.OdorPreventSystem.domain.solution.module.inputFactor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto;

@Controller
@SessionAttributes("inputdata")
public class InputFactorTabController {
	@Autowired //InputFactorService를 자동으로 주입. 데이터 초기화 및 업데이트 기능
	private InputFactorService inputFactorService;

	@ModelAttribute("inputdata")//"inputdata"라는 이름의 모델 속성을 초기화
	public UserInputDataDto.Response getInputData(){ 
		// InputFactorService를 사용하여 UserInputDataDto.Response 객체를 초기화
		return inputFactorService.initInputData();
	}
	
	// "/soms/page5/input" 경로에 GET 요청이 들어왔을 때 호출되는 메서드
	@GetMapping("/soms/page5/input")
	// 세션에 "inputdata"라는 이름으로 저장된 객체를 가져옴
	public ModelAndView openInputPage(@SessionAttribute(name = "inputdata", required = false) UserInputDataDto.Response res ,
	@RequestParam(name ="searchClicked", required= false, defaultValue="false")boolean searchClicked,
	@RequestParam(name = "companyIndex", required = false) Long companyIndex,
	@RequestParam(name="fileName", required = false) String fileName, Model model) throws Exception {

		// "input"이라는 이름의 뷰를 반환하는 ModelAndView 객체를 생성
		ModelAndView mv = new ModelAndView("/solution/input");
		
		try {
            // 데이터 업데이트만 수행하고 초기화는 수행하지 않음
            UserInputDataDto.Response updatedRes = inputFactorService.handleInputData(res, fileName, companyIndex);
			// 물질 값이 모두 0인지 확인
             boolean allZero = inputFactorService.checkIfAllValuesAreZero(updatedRes);

			 // 결과를 모델에 추가하여 클라이언트 측에서 사용할 수 있게 함
            model.addAttribute("inputdata", updatedRes);
			model.addAttribute("allZero", allZero);
			
			
			if(searchClicked && allZero) {
				model.addAttribute("message", "사업장에 입력된 물질정보가 없습니다");
			}else{
				model.addAttribute("message", "");
			}
		
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while processing the request.");
        }

		return mv;
	}
		//초기화 요청을 처리
		@PostMapping("/soms/page5/input/reset")
		public RedirectView resetInputData(@ModelAttribute("inputdata") UserInputDataDto.Response inputData) {
			 // 입력 데이터 초기화
			 inputFactorService.resetInputData(inputData); 
		 
			 // input 페이지로 리다이렉트
			 return new RedirectView("/soms/page5/input");
		}
		
	
	
	//이론적 희석배수 조회//

	// HTTP 응답 본문에 직접 데이터를 작성함
	@ResponseBody
	// "/soms/page5/input" 경로에 POST 요청이 들어왔을 때 호출되는 메서드
	@PostMapping("/soms/page5/input")
     public List<InputFactorDto.DilutionFactorResponse> updateData( 
		@SessionAttribute(name = "inputdata") UserInputDataDto.Response res, //세션에 "inputdata"라는 이름으로 저장된 객체 가져옴
		UserInputDataDto.Request inputdatareq) throws Exception {//클라이언트로부터 전달된 요청 데이터 받음
		inputFactorService.saveData(inputdatareq, res);	//입력한 데이터를 저장															
		List<InputFactorDto.DilutionFactorResponse> resList = inputFactorService.calculateDilutionFactors(res);	//이론적 희석배수, 기여율 계산
																												//악취기여물질 이름, 이론적 희석배수, 기여율 데이터를 담은 객체를 리턴
		//System.out.println(resList.toString()); //for debug
	
		return resList; 	//리스트를 웹 페이지에 던지기
							//ajax로 수신
	}
     
	
}
 