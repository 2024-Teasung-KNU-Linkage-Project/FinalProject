package com.OdorPreventSystem.domain.solution.module.company;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;
import com.OdorPreventSystem.domain.solution.module.company.entity.Company;
import com.OdorPreventSystem.domain.solution.module.company.repository.CompanyRepository;
import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorSubstanceRepository;
import com.OdorPreventSystem.domain.trackingByCar.dto.chemicalDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.OdorPreventSystem.domain.solution.module.search.ChemicalDataDTO;
import com.OdorPreventSystem.domain.solution.module.search.PlaceDataDTO;

@Service
public class CompanyService { 


    private final CompanyRepository companyRepository;
    private OdorSubstanceRepository odorSubstanceRepository; //악취기여물질 가져오기
    private OdorProofSystemMapper mapper;


    //생성자 주입을 통해 RestTemplate을 초기화
    @Autowired
    public CompanyService(OdorSubstanceRepository odorSubstanceRepository, OdorProofSystemMapper mapper, CompanyRepository companyRepository) {
        this.odorSubstanceRepository = odorSubstanceRepository;
        this.mapper = mapper;
        this.companyRepository = companyRepository;
    }

    // 외부 API 호출을 통해 사업장 정보를 가져오는 메서드
    public List<PlaceDataDTO>  getCompaniesFromExternalApi(String searchTerm) {
        List<Company> companies = companyRepository.findAllByNameContaining(searchTerm);
        return companies.stream()
                .map(company -> company.toPlaceDataDTO())
                .collect(Collectors.toList());
    }

    //악취기여물질 농도 값을 가져오기 위한 매서드
    public List<ChemicalDataDTO> getChemicalData(String fileName, Long companyIndex) {
        try {

            List<chemicalDataDTO> companyCSVdata;

            if (fileName != null) {
                companyCSVdata= mapper.getPlaceCsvContent(fileName);
            }else{
                companyCSVdata = mapper.getPlaceCsvContentByCompany(companyIndex);
            }

            List<ChemicalDataDTO> chemicalDataDTOs = new ArrayList<>();

            // DB에서 악취물질 목록 로드
            List<OdorSubstance> validOdorSubstances = StreamSupport.stream(odorSubstanceRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            //화학 데이터 필터링 및 정보 결합
            for (chemicalDataDTO companyData : companyCSVdata) {
                String chemicalName = companyData.getChemicalName();

                List<OdorSubstance> matchingSubstances = validOdorSubstances.stream()
                        .filter(odorSubstance -> odorSubstance.getName_en().trim().toLowerCase().equalsIgnoreCase(chemicalName))
                        .collect(Collectors.toList());

                // 일치하는 OdorSubstance가 있는 경우에만 처리
                if (!matchingSubstances.isEmpty()) {
                    for (OdorSubstance matchingSubstance : matchingSubstances) {
                        ChemicalDataDTO dto = new ChemicalDataDTO();
                        dto.setChemicalName(chemicalName);//영어 이름 설정
                        dto.setChemicalNameKr(matchingSubstance.getName());//한글이름 설정

                        // float 값을 BigDecimal로 변환하고 소수점 처리
                        float rawValue = ((Number) companyData.getChemicalValue()).floatValue();
                        BigDecimal chemicalValue = BigDecimal.valueOf(rawValue);
                        chemicalValue = chemicalValue.setScale(2, RoundingMode.HALF_UP);
                        dto.setChemicalValue(chemicalValue);


                        chemicalDataDTOs.add(dto);
                        //  // DTO 상태 출력
                        //  System.out.println("필터링 된 DTO:");
                        //  System.out.println("Chemical Name (KR): " + dto.getChemicalNameKr());
                        //  System.out.println("Chemical Value: " + dto.getChemicalValue());

                    }
                }
            }
            return chemicalDataDTOs;

        } catch (Exception e) {
            System.out.println("안됐어요");
            return new ArrayList<>();
        }
    }
 }



