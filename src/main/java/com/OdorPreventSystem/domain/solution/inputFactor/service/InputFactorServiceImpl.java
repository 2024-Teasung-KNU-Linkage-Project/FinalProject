package com.OdorPreventSystem.domain.solution.inputFactor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


import com.OdorPreventSystem.domain.solution.company.CompanyService;
import com.OdorPreventSystem.domain.solution.inputFactor.InputFactorService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.search.ChemicalDataDTO;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.AdditionalFeatureDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InterferenceFactorDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.UserInputDataDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InputFactorDto.DilutionFactorResponse;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InputFactorDto.Entity;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.UserInputDataDto.Request;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.UserInputDataDto.Response;
import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;

@Service
@RequiredArgsConstructor
public class InputFactorServiceImpl implements InputFactorService {

	@Autowired
	private OdorProofSystemMapper odorProofSystemMapper;

	@Autowired
    private CompanyService companyService;

	@Override
	//DB에서 입력인자 정보 불러오기
	public List<InputFactorDto.Entity> selectInpList() {
		return odorProofSystemMapper.selectInpList();
	}

	@Override
	//DB에서 방해인자 정보 불러오기
	public List<InterferenceFactorDto.Response> selectInfList() {
		return odorProofSystemMapper.selectInfList();
	}

	@Override
	//UserInputDataDto.Response 객체를 초기화하고 필요한 데이터로 채우는 역할
	public UserInputDataDto.Response initInputData() {
		UserInputDataDto.Response inputdata = new UserInputDataDto.Response();
		//악취기여물질 정보 불러오기
		List<InputFactorDto.Entity> inpent = this.selectInpList();
		//악취기여물질 카테고리 리스트 불러오기
		inputdata.setCatelist(this.selectCategoryList());
		//카테고리별로 그룹화
		Iterator<String> iterator = inputdata.getCatelist().iterator();
		List<InputFactorDto.Response> list = null;
		Map<Integer, List<InputFactorDto.Response>> map = new LinkedHashMap<Integer, List<InputFactorDto.Response>>();
		int index = 0;
		
		while(iterator.hasNext()) {
			String category = iterator.next();
			list = new ArrayList<InputFactorDto.Response>();
			for(int i = 0; i < inpent.size(); i++) {
				if(inpent.get(i).getCategory().equals(category)) {
					list.add(InputFactorDto.Response.builder()
							.idx(i+1)
							.name(inpent.get(i).getName())
							.value(inpent.get(i).getValue())
							.threshold(inpent.get(i).getThreshold())
							.waterSolubility(inpent.get(i).isWaterSolubility())
							.build());
				}
			}
			map.put(index++, list);
		}
		/* 	for debug : print hashmap
		for(Entry<Integer, List<InputFactorDto.Response>>  entry : map.entrySet()) {
			System.out.print(inputData.getCatelist().get(entry.getKey()) + ":");
			for(int i = 0; i < entry.getValue().size(); i++) {
				System.out.print(entry.getValue().get(i).getName() + " ");
			}
			System.out.println();
		} */
		
		inputdata.setInplist(map); 
		inputdata.setInflist(this.selectInfList()); 
		inputdata.setAddlist(this.selectAddList());
		
		return inputdata;
	}
  



	@Override
	//DB에서 추가 시설 리스트 불러옴
	public List<AdditionalFeatureDto.Response> selectAddList() {
		return odorProofSystemMapper.selectAddList();
	}

	@Override
	//DB에서 입력 인자 카테고리 리스트 불러옴
	public List<String> selectCategoryList() {
		return odorProofSystemMapper.selectCategoryList();
	}

	@Override
	// 데이터 저장
	public void saveData(Request inputdatareq, Response inputdata) {
		//입력 인자 저장
		Map<Integer, List<InputFactorDto.Response>> map = inputdata.getInplist();
		
		for(Entry<Integer, List<InputFactorDto.Response>>  entry : map.entrySet()) {
			List<InputFactorDto.Response> list = entry.getValue();
			for(int i = 0; i < list.size(); i++) {
				list.get(i).setValue(inputdatareq.getInplist().get(entry.getKey()).get(i).getValue());
			}
		}
		//방해인자 저장
		for(int i = 0; i < inputdata.getInflist().size(); i++) {
			inputdata.getInflist().get(i).setValue(inputdatareq.getInflist().get(i).getValue());
			inputdata.getInflist().get(i).setTargetValue(inputdatareq.getInflist().get(i).getTargetValue());
		}
		//추가시설 저장
		for(int i = 0; i < inputdata.getAddlist().size(); i++) {
			inputdata.getAddlist().get(i).setAvailable(inputdatareq.getAddlist().get(i).isAvailable());
		}
		//희석배수, 풍량 데이더 저장
		inputdata.setTargetDilutionFactor(inputdatareq.getTargetDilutionFactor());
		inputdata.setWind(inputdatareq.getWind());
	}

	@Override
	// 희석배수와 기여율 계산
	public List<DilutionFactorResponse> calculateDilutionFactors(UserInputDataDto.Response inputdata) {
		List<DilutionFactorResponse> resList = new ArrayList<>();
		int sum = 0; //이론적 희석배수 합
		
		for(Entry<Integer, List<InputFactorDto.Response>>  entry : inputdata.getInplist().entrySet()) {
			List<InputFactorDto.Response> list = entry.getValue();
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getValue() == 0) continue; 	//입력한 값이 0이면 스킵
				InputFactorDto.Response inp = list.get(i);
				InputFactorDto.DilutionFactorResponse res = new InputFactorDto.DilutionFactorResponse();
				res.setName(inp.getName());	//악취기여물질 이름
				res.setDilutionFactor((int)Math.round(inp.getValue()/inp.getThreshold()));	//이론적 희석배수 계산
				sum += res.getDilutionFactor();	//이론적 희석배수 총합에 더하기
				resList.add(res);
			}
		}
		// 총 희석 배수 설정
		inputdata.setDilutionFactor(sum);
		resList.add(InputFactorDto.DilutionFactorResponse.builder()	//이론적 희석배수 총합을 담은 객체를 리스트에 추가
						.name("total")	  //ajax에서 데이터를 받을 때 name이 "total"이면 총합으로 판단
						.dilutionFactor(sum)						
						.contRate(100)//기여율 100%
						.build());				   					
		
		for(int i = 0; i < resList.size(); i++) {
			InputFactorDto.DilutionFactorResponse res = resList.get(i);
			res.setContRate(Math.round((double)res.getDilutionFactor()/sum*1000)/1000.0); //기여율 계산
		}
		return resList;
	}

	@Override
	//입력 데이터 업데이트
	public void updateInputData(Response res) {
		// 설정 탭에서 수정한 내용 반영
		List<InputFactorDto.Entity> newinplist = this.selectInpList();
		
		for(Entity newinp: newinplist) {
			int catg = odorProofSystemMapper.selectInpCatg(newinp.getIdx());
			for(InputFactorDto.Response inp: res.getInplist().get(catg-1)) {
				if(inp.getIdx() == newinp.getIdx()) {
					inp.setThreshold(newinp.getThreshold());
					inp.setWaterSolubility(newinp.isWaterSolubility());
					break;
				}
			}
		}
		return;
	}



	//악취기여물질정보 받아와 input뷰로 반환
	@Override
    public UserInputDataDto.Response handleInputData(UserInputDataDto.Response res, String fileName, Long companyIndex) throws Exception {

        // res가 null일 경우 초기화
        if (res == null) {
            res = initInputData();
        } else {
		    updateInputData(res);
        }
        // companyIndex가 있는 경우 데이터 업데이트
        if (fileName != null || companyIndex != null) {
            List<ChemicalDataDTO> chemicalDataDTOs = companyService.getChemicalData(fileName, companyIndex);
              updateChemicalValues(res, chemicalDataDTOs);
        }

        return res;
    }

	@Override
	public void resetInputData(UserInputDataDto.Response res) {
        // 모든 값을 기본값으로 초기화
        if (res != null) {
            resetValuesToDefault(res);
        }
		
    }

	private void resetValuesToDefault(UserInputDataDto.Response res) {

		//악취기여물질의 값 초기화
		for (InputFactorDto.Response factor : res.getInplist().values().stream().flatMap(List::stream).collect(Collectors.toList())) {
			factor.setValue(0.0f);
		}
		///방해인자의 값 초기화
		for(InterferenceFactorDto.Response interference : res.getInflist()) {
			interference.setValue(0.0f);
			interference.setTargetValue(0.0f);
		}
		  // 추가 설비의 체크박스 초기화
		  for (AdditionalFeatureDto.Response additionalFeature : res.getAddlist()) {
			additionalFeature.setAvailable(false); // 체크박스를 해제
		}

		// 전체 이론적 희석배수 초기화
    	res.setDilutionFactor(0);

		//목표희석배수와 풍량 값 초기화
		res.setTargetDilutionFactor(0);
		res.setWind(0.0f);

		
	}
	
   //악취기여물질 업데이트
    private void updateChemicalValues(UserInputDataDto.Response res, List<ChemicalDataDTO> chemicalDataDTOs) {
        for (InputFactorDto.Response factor : res.getInplist().values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            String factorName = factor.getName();

            List<ChemicalDataDTO> matchingChemicals = chemicalDataDTOs.stream()
                    .filter(dto -> dto.getChemicalNameKr().equalsIgnoreCase(factorName))
                    .collect(Collectors.toList());

            if (!matchingChemicals.isEmpty()) {
              BigDecimal chemicalValue = matchingChemicals.get(0).getChemicalValue();
              double valueAsDouble = chemicalValue.doubleValue();
              factor.setValue(valueAsDouble);
            } else {
                factor.setValue(0.0f);
            }
        }
    }


// 물질 값이 모두 0인지 확인하는 메서드 구현
	@Override
    public boolean checkIfAllValuesAreZero(UserInputDataDto.Response inputData) {
        // inplist에서 값이 모두 0인지 확인
        return inputData.getInplist().values().stream()
                .flatMap(List::stream)
                // 정확한 필드 이름을 사용하여 값이 0인지 확인
                .allMatch(factor -> factor.getValue() == 0);  
    }

}