package com.OdorPreventSystem.domain.solution.module.company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.OdorPreventSystem.domain.solution.module.search.ChemicalDataDTO;
import com.OdorPreventSystem.domain.solution.module.search.PlaceDataDTO;


@RestController
@RequestMapping("/soms/page5/input")
// @CrossOrigin(origins = "*") //모든 출처 허용
public class CompanyController {

     @Autowired    
     private final CompanyService companyService;

     public CompanyController(CompanyService companyService){
          this.companyService = companyService; 
    }
     //사업장 자동완성요청
     @GetMapping("/autocomplete")
      public List<PlaceDataDTO> searchCompany(@RequestParam("name") String searchTerm) {
            // 서비스의 searchCompany 메서드를 호출하여 검색 결과를 반환
        return companyService.getCompaniesFromExternalApi(searchTerm);
      } 
      
      //악취기여물질 요청
      @GetMapping("/detailValue")
      public List<ChemicalDataDTO> getCompanyDetails(@RequestParam("fileName") String fileName){
            return companyService.getChemicalData(fileName, null);
      }
}
