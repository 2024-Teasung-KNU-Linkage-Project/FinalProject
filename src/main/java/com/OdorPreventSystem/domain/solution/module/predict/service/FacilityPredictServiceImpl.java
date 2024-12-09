package com.OdorPreventSystem.domain.solution.module.predict.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.OdorPreventSystem.domain.solution.module.predict.FacilityPredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.module.predict.dto.AvailableFacilityDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityCombination;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityConditionDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto.Entity;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityDto.FindCombination;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency;
import com.OdorPreventSystem.domain.solution.module.predict.dto.FacilityEfficiency.InfEff;
import com.OdorPreventSystem.domain.solution.module.predict.dto.PredictFacilityDto;
import com.OdorPreventSystem.domain.solution.module.predict.dto.PredictFacilityDto.Request;
import com.OdorPreventSystem.domain.solution.module.predict.dto.PredictFacilityDto.Response;
import com.OdorPreventSystem.domain.solution.module.predict.dto.TargetFactorList;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InterferenceFactorDto;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.InterferenceFactorDto.Target;
import com.OdorPreventSystem.domain.solution.module.inputFactor.dto.UserInputDataDto;
import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;

@Service
public class FacilityPredictServiceImpl implements FacilityPredictService {
	@Autowired
	private OdorProofSystemMapper odorProofSystemMapper;
	
	static List<FacilityDto.Entity> facilityList = null;
	List<FacilityCombination> comList = null;

	@Override
	public List<FacilityDto.Entity> selectFacilityList() {
		return odorProofSystemMapper.selectFacilityList();
	}

	@Override
	public Response initPredictDto() {
		// PredictFacilityDto.Response 객체를 생성
		PredictFacilityDto.Response res = new PredictFacilityDto.Response();
		facilityList = this.selectFacilityList();
		
		//시설 종류별로 분류
		List<FacilityDto.Response> prelisttmp = new ArrayList<>();
		List<FacilityDto.Response> prooflisttmp = new ArrayList<>();
		List<FacilityDto.Response> postlisttmp = new ArrayList<>();
		List<FacilityDto.Response> carbonlisttmp = new ArrayList<>();

		// 순회하면서 시설 정보를 추출하고 카테고리별로 분류
		for(int i = 0; i < facilityList.size(); i++) {
			FacilityDto.Response tmpres = FacilityDto.Response.builder()
					.fac_idx(facilityList.get(i).getFac_idx())
					.name(facilityList.get(i).getName())
					.available(true)
					.hidden(false) 
					.build();
			
            //시설 카테고리에 분류
			switch(facilityList.get(i).getCategory()) {
				case 1: prelisttmp.add(tmpres); break;
				case 2: prooflisttmp.add(tmpres); break;
				case 3: postlisttmp.add(tmpres); break;
				case 4: carbonlisttmp.add(tmpres); break;
			}
		}
		//응답 객체에 각 분류된 리스트 설정
		res.setPrefacilityList(prelisttmp);
		res.setProoffacilityList(prooflisttmp);
		res.setPostfacilityList(postlisttmp);
		res.setCarbonfacilityList(carbonlisttmp);
		
		return res;
	}
	
	@Override
	public void updateFacility() {
		// 수정한 내용 반영
		if(facilityList == null) return;
		List<Entity> newFacilityList = this.selectFacilityList();
		
		for(Entity newFacility: newFacilityList) {
			for(Entity facility: facilityList) {
				if(facility.getFac_idx() == newFacility.getFac_idx()) {
					facility.setCostBase(newFacility.getCostBase());
					facility.setCostByWind(newFacility.getCostByWind());
					break;
				}
			}
		}
	}
	

	@Override
	//사용자 입력 데이터를 기반으로 시설 조합을 찾아 반환
	public List<FacilityCombination> findCombinations(UserInputDataDto.Response inputdata, PredictFacilityDto.Response facility) {
		/* step 1: 시설사용조건 체크 */ //확인해야할것 : requiredAdditionalFeature, db에서 infcondition, inpcondition
									//방지시설 한정 targetfactor
									//사용가능한 시설 리스트 반환
		List<AvailableFacilityDto> prelist = this.conditionCheck(inputdata, facility.getPrefacilityList()); //전처리
		List<AvailableFacilityDto> prooflist = this.conditionCheckProof(inputdata, facility.getProoffacilityList()); //방지
		List<AvailableFacilityDto> postlist = this.conditionCheck(inputdata, facility.getPostfacilityList()); //후처리
		//탄소흡착시설은 이산화탄소 존재해야 사용
		List<AvailableFacilityDto> carbonlist = this.conditionCheckProof(inputdata, facility.getCarbonfacilityList());
		
		/* step 2: 검사해야할 입력인자, 방해인자 목록(농도 입력 값 0 아님)*/
		// 입력 인자와 방해 인자를 필터링하여 유효한 항목만을 리스트에 추가
		TargetFactorList targetData = new TargetFactorList();
		List<InputFactorDto.Target> inplist = new ArrayList<>();//유효한 입력인자 리스트
		List<InterferenceFactorDto.Target> inflist = new ArrayList<>(); //유효한 방해인자 리스트
		
		// //이론적 희석배수 > 목표 희석배수이면 유효한 입력 인자(값이 0보다 큰 경우)만을 선별하여 inplist 리스트에 추가 
		// if(inputdata.getDilutionFactor() > inputdata.getTargetDilutionFactor()) {
		// 	for(Entry<Integer, List<InputFactorDto.Response>>  entry : inputdata.getInplist().entrySet()) {
		// 		for(int i = 0; i < entry.getValue().size(); i++) {
		// 			InputFactorDto.Response tmp = entry.getValue().get(i);
		// 			if(tmp.getValue() > 0) {
		// 				//유효한 입력인지 추가
		// 				InputFactorDto.Target inp = new InputFactorDto.Target();
		// 				inp.setIdx(tmp.getIdx()); 
		// 				inp.setValue(tmp.getValue()); 
		// 				inp.setThreshold(tmp.getThreshold());
		// 				inp.setWaterSolubility(tmp.isWaterSolubility());
		// 				inplist.add(inp);
		// 			}
		// 		}
		// 	}
		// }
		// 유효한 입력 인자(값이 0보다 큰 경우)만을 선별하여 inplist 리스트에 추가 
		
			for(Entry<Integer, List<InputFactorDto.Response>>  entry : inputdata.getInplist().entrySet()) {
				for(int i = 0; i < entry.getValue().size(); i++) {
					InputFactorDto.Response tmp = entry.getValue().get(i);
					if(tmp.getValue() > 0) {
						//유효한 입력인지 추가
						InputFactorDto.Target inp = new InputFactorDto.Target();
						inp.setIdx(tmp.getIdx()); 
						inp.setValue(tmp.getValue()); 
						inp.setThreshold(tmp.getThreshold());
						inp.setWaterSolubility(tmp.isWaterSolubility());
						inplist.add(inp);
					}
				}
			}
		
		// //방해인자 값 > 목표값이면 그것을 새로운 리스트 inflist에 추가하는 로직
		// for(int i = 0; i < inputdata.getInflist().size(); i++) {
		// 	InterferenceFactorDto.Response tmp = inputdata.getInflist().get(i);
		// 	if(tmp.getValue() > tmp.getTargetValue()) {
		// 		//유효한 방해 인자 추가
		// 		InterferenceFactorDto.Target inf = new InterferenceFactorDto.Target();
		// 		inf.setIdx(tmp.getIdx()); 
		// 		inf.setValue(tmp.getValue()); 
		// 		inf.setTargetValue(tmp.getTargetValue());
		// 		inflist.add(inf);
		// 	}
		// }
		
		//방해인자값 추가
		for(int i = 0; i < inputdata.getInflist().size(); i++) {
			InterferenceFactorDto.Response tmp = inputdata.getInflist().get(i);
				//유효한 방해 인자 추가
				InterferenceFactorDto.Target inf = new InterferenceFactorDto.Target();
				inf.setIdx(tmp.getIdx()); 
				inf.setValue(tmp.getValue()); 
				inf.setTargetValue(tmp.getTargetValue());
				inflist.add(inf);	
		}
		
		//필터링된 입력 인자와 방해 인자 목록
		targetData.setInplist(inplist);
		targetData.setInflist(inflist);
		targetData.setDilutionFactor(calculateActualDilutionFactor(inplist));
		targetData.setTargetDilutionFactor(inputdata.getTargetDilutionFactor());
		targetData.setCombination(new ArrayList<FacilityDto.FindCombination>());
		
		/* step 4: 시설 우선순위 오름차순으로 정렬 */
		Collections.sort(prelist);
		Collections.sort(prooflist);
		Collections.sort(postlist);
		Collections.sort(carbonlist);
		//System.out.println(prelist);
		//System.out.println(prooflist);
		//System.out.println(postlist);
		//System.out.println(carbonlist);
		//System.out.println(inplist);
		//System.out.println(inflist);
		//System.out.println(targetData.getDilutionFactor());
		//System.out.println(targetData.getTargetDilutionFactor());
		
		/* step 5: 시설 조합 찾기 */
		List<FacilityCombination> comlist = new ArrayList<>();// 결과 조합 리스트
		//전처리 단계에서 가능한 조합 찾기
		for(TargetFactorList prep: preProcess(targetData, prelist)) {
			TargetFactorList proofTargetData = prep;
			//방지 단계에서 가능한 조합 찾기
			for(TargetFactorList proofp: proofProcess(proofTargetData, prooflist)) {
				TargetFactorList postTargetData = proofp;
				//후처리 단계에서 가능한 조합 찾기
				for(TargetFactorList postp: postProcess(postTargetData, postlist)) {
					/* 결과를 comlist에 복사 */
					if(!postp.getCombination().isEmpty()) {
						if(!validateCombination(postp)) continue; //유효성 검사를 통과 해야 조합에 추가

						FacilityCombination comb = new FacilityCombination();
						List<FacilityDto.CombinationResponse> resList = new ArrayList<>();// 각 조합의 세부정보 리스트
						BigInteger costSum = BigInteger.ZERO; //조합의 총 비용

						//각 시설 조합에 대한 세부정보 설정
						for(FindCombination tmp: postp.getCombination()) {
							FacilityDto.CombinationResponse res = new FacilityDto.CombinationResponse();
							res.setFac_idx(tmp.getFac_idx());
							res.setName(SearchName(tmp.getFac_idx()));
							//시설 카테고리에 따라 이름 설정
							switch(tmp.getCategory()) {
								case 1: res.setCategory("전처리시설"); break;
								case 2: res.setCategory("방지시설"); break;
								case 3: res.setCategory("후처리시설"); break;
								case 4: res.setCategory("탄소흡착시설"); break;
							}
							//시설비용 계산
							double wind = inputdata.getWind();
							res.setCost(this.searchAndCalcCost(tmp.getFac_idx(), wind));
							costSum = costSum.add(res.getCost()); //총 비용 계산
							res.setImagesrc(SearchImagesrc(tmp.getFac_idx())); // 시설이미지 
							resList.add(res);
						}


						//조합정보 설정
						comb.setCombination(resList);
						comb.setCostTotal(costSum);// 조합의 총 비용 설정
						comb.setDilutionFactor((int)Math.round(postp.getDilutionFactor())); //희석배수 설정
						comlist.add(comb);
					}
				}
				
			}
		}
		comList = comlist;
		return comList;
	}
	

    //시설 찾고 기본비용 및 풍량에 따른 비용 계산
	private BigInteger searchAndCalcCost(int idx, double wind) {
		if(facilityList == null) return BigInteger.ZERO;
		
		for(int i = 0; i < facilityList.size(); i++) {
			if(idx == facilityList.get(i).getFac_idx()) {
				//풍량에 따른 추가비용 계산
				double costByWind = facilityList.get(i).getCostByWind().doubleValue() * wind;
				//기본비용
				BigInteger cost = facilityList.get(i).getCostBase();
				//최총단가
				return cost.add(BigDecimal.valueOf(costByWind).toBigInteger());
			}
		}
		return BigInteger.ZERO;
	}
    //희석배수 계산
	private double calculateActualDilutionFactor(List<InputFactorDto.Target> inplist) {
		double sum = 0;
		for(int i = 0; i < inplist.size(); i++) {
			InputFactorDto.Target tmp = inplist.get(i);
			sum += tmp.getValue()/tmp.getThreshold();
		}
		return sum;
	}
	
	// 방해 인자 처리용 메서드
	private List<TargetFactorList> preProcess(TargetFactorList inputData, List<AvailableFacilityDto> prelist) {
		/* 리스트 생성: 방해인자 값, 목표값 리스트, 시설조합 담고 있음 */
		/* preProcess 한정 */
		List<TargetFactorList> targetList = new ArrayList<TargetFactorList>();
		targetList.add(inputData);
		
		for(InterferenceFactorDto.Target target: inputData.getInflist()) {
			if(target.getTargetValue() == 0) continue;

			Iterator<TargetFactorList> it = targetList.iterator();
			List<TargetFactorList> outputList = new ArrayList<TargetFactorList>();
			while(it.hasNext()) {
				TargetFactorList targetData = it.next();
				//System.out.println(targetData.getCombination());
				List<TargetFactorList> tmpOutputList = new ArrayList<TargetFactorList>();
				//target 제거 가능한 시설 목록
				List<Integer> facList = odorProofSystemMapper.selectUsableFacInf(target.getIdx());
				preProcessSearch(target, targetData, prelist, facList , tmpOutputList);
				/* outputList 뒤에 tmpOutputList 붙이기 */
				for(int i = 0; i < tmpOutputList.size(); i++) {
					if(!tmpOutputList.get(i).getCombination().isEmpty()) outputList.add(tmpOutputList.get(i));
				}
			}
			if(!outputList.isEmpty())targetList = outputList;
			//System.out.println(targetList);
		}
		return targetList;
	}
    //전처리시설의 입력된 방해 인자와 관련된 시설 목록을 처리하고 조합을 찾기 위해 재귀적으로 호출
	private void preProcessSearch(
		                          InterferenceFactorDto.Target target, //방해인자 정보
		                          TargetFactorList targetData, //현재까지 처리된 방해인자와 시설조합
		                          List<AvailableFacilityDto> prelist, //전처리 시설 목록
		                          List<Integer> facList,//방해인자에 사용할 수 있는 시설 목록
			                      List<TargetFactorList> targetDataList //결과담을 리스트
								  ) {
		
		if(facList == null) return; //null이면 종료
		
		/* get target index */
		int n = 0;
		for(InterferenceFactorDto.Target t: targetData.getInflist()) {
			if(t.getIdx() == target.getIdx()) break; //찾으면 종료
			n++;
		}
		
		//System.out.println()
		/* 전처리 시설 목록 복사*/
		List<AvailableFacilityDto> copiedPrelist = new ArrayList<AvailableFacilityDto>();
		for(AvailableFacilityDto fac: prelist) {
			try {
				copiedPrelist.add(fac.clone());
			} catch (CloneNotSupportedException e) {
	
				e.printStackTrace(); //클론지원x => 예외출력
			}
		}
		  // 사용 가능한 시설을 처리
		int num = 0;
		for(AvailableFacilityDto fac: copiedPrelist) {
			fac.setAvailable(false); //초기 모두 사용불가 처리
			for(Integer usableFac: facList) {
				if(fac.getFac_idx() == usableFac) {
					// 현재 방해인자의 목표값 보다 큰 경우 사용 가능으로 설정
					if(targetData.getInflist().get(n).getTargetValue() < targetData.getInflist().get(n).getValue()) {
						fac.setAvailable(true);
						num++;
					}
					break;
				}
			}
		}
		
		// 사용 가능한 시설이 없으면 현재 targetData를 결과 리스트에 추가하고 종료
		if(num == 0) {
			//System.out.println(targetData.getCombination());
			if(!targetData.getCombination().isEmpty()) targetDataList.add(targetData);
			return;
		}
		/* 사용가능한 시설처리 */
		for(Iterator<AvailableFacilityDto> it = copiedPrelist.iterator(); it.hasNext();) {
			AvailableFacilityDto fac = it.next();
			if(fac.isAvailable() == false) continue; //사용불가능한 시설 건너뜀
			float efficiency = 0;
			int used = 0;

			/* 현재 시설이 몇 번 사용되었는지 계산*/
			if(targetData.getCombination() != null) {
				for(FacilityDto.FindCombination tmp: targetData.getCombination()) {
					if(tmp.getFac_idx() == fac.getFac_idx()) used++;
				}
			}
			
			if(used >= 3) continue; //같은 시설 3회 초과 중복 방지
			
			/* run */
			//deepcopy targetData
			TargetFactorList newTargetData = targetData.deepCopy();
			//use facility
			//시설의 모든 제거 물질과 효율 list 가져오기
			List<FacilityEfficiency.InfEff> effList = odorProofSystemMapper.selectInfEff(fac.getFac_idx());
			//run
			boolean efficiencyIsZero = false; //제거효율 0인지 확인

			//각 방해 인자의 제거 효율을 업데이트
			for(InfEff eff: effList) {
				for(InterferenceFactorDto.Target inf: newTargetData.getInflist()) {
					if(inf.getIdx() == eff.getInf_idx()) {
						// 사용 횟수에 따른 제거 효율 설정
						if(used == 0) efficiency = eff.getEfficiency1();
						else if(used == 1) efficiency = eff.getEfficiency2();
						else if(used >= 2) efficiency = eff.getEfficiency3();
						// 제거 효율에 따라 간섭 인자의 값 조정
						if(efficiency > 0)inf.setValue(inf.getValue()*(1-efficiency));
						else efficiencyIsZero = true;
						break;
					}
				}
			}
			if(efficiencyIsZero) {
				it.remove();
				continue; //제거효율이 0이면 이 시설은 더 이상 사용x
			}
			//조합에 시설 추가
			newTargetData.getCombination().add(new FindCombination(fac.getFac_idx(), 1));
			//재귀 호출
			preProcessSearch(target, newTargetData, copiedPrelist, facList, targetDataList);
			it.remove(); /* 우선순위 적용: facilityA(lowPriority) - facilityB(highPriority) 같은 조합은 나오지 않음*/

		}
	}
	
	private List<TargetFactorList> proofProcess(
		                                        TargetFactorList proofTargetData, //방지 시설 관련 데이터 
												List<AvailableFacilityDto> prooflist //방지시설 목록
												) {
		List<TargetFactorList> targetList = new ArrayList<TargetFactorList>();
		targetList.add(proofTargetData);
		
		//System.out.println(proofTargetData);

        //  // 방지 시설 목표 희석 배수가 0이면 현재의 targetList를 반환
		if(proofTargetData.getTargetDilutionFactor() == 0) return targetList;
		
		boolean[] tlist = {true, false};
		for(boolean target: tlist) { //수용성, 비수용성에 대해
			Iterator<TargetFactorList> it = targetList.iterator();
			List<TargetFactorList> outputList = new ArrayList<TargetFactorList>();

			while(it.hasNext()) {
				TargetFactorList targetData = it.next();
				//System.out.println(targetData.getCombination());
				List<TargetFactorList> tmpOutputList = new ArrayList<TargetFactorList>();
				//target 제거 가능한 시설 목록
				List<Integer> facList = null;
				if(target == true) facList = odorProofSystemMapper.selectUsableFacWaterSoluble();
				else facList = odorProofSystemMapper.selectUsableFacWaterUnsoluble();
				//System.out.println(facList);

				//현재 방지 인자에 맞는 시설 목록을 처리
				proofProcessSearch(target, targetData, prooflist, facList , tmpOutputList);
				//System.out.println(tmpOutputList);


				for(int i = 0; i < tmpOutputList.size(); i++) {
					if(!tmpOutputList.get(i).getCombination().isEmpty()) outputList.add(tmpOutputList.get(i));
				}
			}
			if(!outputList.isEmpty())targetList = outputList;
		}
		return targetList;
	}
    
	//주어진 방지 시설 목록에서 사용 가능한 시설을 찾아내고, 각 시설의 효율성을 고려하여 조합을 만듦
	private void proofProcessSearch(
		                            boolean target, //수용성 ,비수용성 시설 여부
									TargetFactorList targetData, //현재 타겟 인자 및 시설 조합 정보
									List<AvailableFacilityDto> prooflist, //방지 시설 목록
									List<Integer> facList, // 사용 가능한 시설 목록
									List<TargetFactorList> targetDataList //결과 담을 리스트
									) {
		if(facList == null) return;
		
		//System.out.println(prooflist);
		
		/* deepcopy prooflist */
		List<AvailableFacilityDto> copiedProoflist = new ArrayList<AvailableFacilityDto>();
		for(AvailableFacilityDto fac: prooflist) {
			try {
				copiedProoflist.add(fac.clone());
			} catch (CloneNotSupportedException e) {
	
				e.printStackTrace();
			}
		}
		
		//사용 가능한 시설 목록 기반으로 facility 설정
		int num = 0;
		for(AvailableFacilityDto fac: copiedProoflist) {
			fac.setAvailable(false);
			for(Integer usableFac: facList) {
				if(fac.getFac_idx() == usableFac) {
					//목표희석배수가 현재 희석배수보다 작으면 사용가능으로 설정
					if(targetData.getTargetDilutionFactor() < targetData.getDilutionFactor()) {
						fac.setAvailable(true);
						num++;
					}
					break;
				}
			}
		}
		//System.out.println(copiedProoflist);
		
		if(num == 0) {
			//System.out.println(targetData.getCombination());
			if(!targetData.getCombination().isEmpty()) targetDataList.add(targetData);
			return;
		}
		
		/* can use facility */
		//사용가능한 시설로 조합 만듦
		for(Iterator<AvailableFacilityDto> it = copiedProoflist.iterator(); it.hasNext();) {
			AvailableFacilityDto fac = it.next();
			if(fac.isAvailable() == false) continue; //사용불가능한 시설 건너뜀
			float efficiency = 0;
			int used = 0;
			/* count facility used number */

			//현재 시설 몇 번 사용되었는지 계산
			if(targetData.getCombination() != null) {
				for(FacilityDto.FindCombination tmp: targetData.getCombination()) {
					if(tmp.getFac_idx() == fac.getFac_idx()) used++;
				}
			}
			
			if(used >= 3) continue; //같은 시설 3회 초과 중복 방지
			
			/* run */
			//deepcopy targetData
			TargetFactorList newTargetData = targetData.deepCopy();
			//use facility

			// 시설의 효율성 목록
			List<FacilityEfficiency.InpEff> efficiencies = odorProofSystemMapper.selectInpEff(fac.getFac_idx());
			FacilityEfficiency.InpEff eff = efficiencies.get(0);
			//System.out.println(eff);
			//run
			boolean efficiencyIsZero = false;

			// 입력 인자에 대한 효율성을 적용하여 값 조정
			for(InputFactorDto.Target inp: newTargetData.getInplist()) {
				//if(eff.getTarget() == null) //전부
				if(efficiencies.size() == 1 && eff.getTarget().equals(1) && !inp.isWaterSolubility()) continue; //수용성
				if(efficiencies.size() == 1 && eff.getTarget().equals(0) && inp.isWaterSolubility()) continue; //비수용성
				
				
				// 사용 횟수에 따른 효율성 설정
				if(used == 0) efficiency = eff.getEfficiency1();
				else if(used == 1) efficiency = eff.getEfficiency2();
				else if(used >= 2) efficiency = eff.getEfficiency3();
				if(efficiency > 0) {
					double dfSubtract = inp.getValue()*efficiency/inp.getThreshold();
					inp.setValue(inp.getValue()*(1-efficiency));
					//update dilutionfactor 
					newTargetData.setDilutionFactor(newTargetData.getDilutionFactor() - dfSubtract);
				}
				else {
					efficiencyIsZero = true; break;
				}
				
			}
			if(efficiencyIsZero) {
				if(used == 0 && (fac.getPriority() == 1 || fac.getPriority() == 2)) break; //Scrubber H2SO4, Scrubber NaOH 우선순위 적용
				it.remove();
				continue; //제거효율이 0이면 이 시설은 더 이상 사용x
			}
			//add to combination
			newTargetData.getCombination().add(new FindCombination(fac.getFac_idx(), 2));
			/*
			for(FindCombination comb: newTargetData.getCombination()) {
				System.out.println(comb.getFac_idx());
			}
			System.out.println(copiedProoflist);
			System.out.println("DF : " + newTargetData.getDilutionFactor());
			*/
			//call recursive
			proofProcessSearch(target, newTargetData, copiedProoflist, facList, targetDataList);
			if(used == 0 && (fac.getPriority() == 1 || fac.getPriority() == 2)) break; //Scrubber H2SO4, Scrubber NaOH 우선순위 적용
			it.remove(); /* 우선순위 적용: facilityA(lowPriority) - facilityB(highPriority) 같은 조합은 나오지 않음*/
		}
	}
	//TargetFactorList와 시설 목록을 기반으로 후처리 작업을 수행
	private List<TargetFactorList> postProcess(	
												TargetFactorList postTargetData, //후처리 대상이 되는 타겟 인자 및 시설 조합 정보
												List<AvailableFacilityDto> postlist // 후처리할 수 있는 시설 목록
												) {
		/* 리스트 생성: 방해인자 값, 목표값 리스트, 시설조합 담고 있음 */
		List<TargetFactorList> targetList = new ArrayList<TargetFactorList>();
		targetList.add(postTargetData);
		
		 // 방해 인자 리스트를 순회하면서 처리
		for(InterferenceFactorDto.Target target: postTargetData.getInflist()) {
			if(target.getTargetValue() == 0) continue;
			Iterator<TargetFactorList> it = targetList.iterator();  // 현재 타겟 리스트를 순회하기 위한 iterator
			List<TargetFactorList> outputList = new ArrayList<TargetFactorList>();// 처리 결과를 담을 리스트
			while(it.hasNext()) {
				TargetFactorList targetData = it.next();
				//System.out.println(targetData.getCombination());
				List<TargetFactorList> tmpOutputList = new ArrayList<TargetFactorList>();
				
				// 방해 인자에 해당하는 시설 목록을 가져오기
				List<Integer> facList = odorProofSystemMapper.selectUsableFacInf(target.getIdx());
				postProcessSearch(target, targetData, postlist, facList , tmpOutputList);

				/* outputList 뒤에 tmpOutputList 붙이기 */
				for(int i = 0; i < tmpOutputList.size(); i++) {
					if(!tmpOutputList.get(i).getCombination().isEmpty()) outputList.add(tmpOutputList.get(i));
				}
				//System.out.println(outputList);
			}
			if(!outputList.isEmpty())targetList = outputList;
		}
		//CO2 값이 있으면 탄소흡착시설 붙이기
		for(TargetFactorList target1: targetList) {
			for(InputFactorDto.Target inp: target1.getInplist()) {
				if(odorProofSystemMapper.isCO2(inp.getIdx()) && inp.getValue() > 0) {
					 // 시설 목록에서 탄소 흡착 시설을 찾기
					for(int i = facilityList.size() - 1; i >= 0; i--) {
						Entity fac = facilityList.get(i);
						if(fac.getCategory() == 4) {// 탄소 흡착 시설 카테고리 확인
							double dfSubtract = inp.getValue()*(0.9);// 제거 효율 계산
							inp.setValue(inp.getValue()*(0.1));// 남은 값 업데이트
							target1.getCombination().add(new FindCombination(fac.getFac_idx(), 4));// 조합에 추가
							target1.setDilutionFactor(target1.getDilutionFactor()-dfSubtract); // 희석 배수 업데이트
							break;
						}
					}
					break;
				}
			}
		}
		return targetList;
	}
	
	
	
	
   //주어진 타겟 인자와 시설 목록을 기반으로 후처리 작업을 수행하여 최적의 시설 조합 검색
	private void postProcessSearch(
            InterferenceFactorDto.Target target, //방해인자
            TargetFactorList targetData, // 타겟 데이터 및 시설 조합 정보
            List<AvailableFacilityDto> postlist, //후처리 할 수 있는 목록
            List<Integer> facList, // 사용가능한 시설
            List<TargetFactorList> targetDataList
									) {
		
		if(facList == null) return;
		
		 // 타겟 인자 리스트에서 현재 타겟의 인덱스를 찾기
		int n = 0;
		for(InterferenceFactorDto.Target t: targetData.getInflist()) {
			if(t.getIdx() == target.getIdx()) break;
			n++;
		}
		
		//System.out.println()
		/* deepcopy prelist */
		List<AvailableFacilityDto> copiedPostlist = new ArrayList<AvailableFacilityDto>();
		for(AvailableFacilityDto fac: postlist) {
			try {
				copiedPostlist.add(fac.clone());
			} catch (CloneNotSupportedException e) {
		
				e.printStackTrace();
			}
		}
		
		int num = 0;// 사용 가능한 시설 수 카운트
		for(AvailableFacilityDto fac: copiedPostlist) {
			fac.setAvailable(false);
			for(Integer usableFac: facList) {
				if(fac.getFac_idx() == usableFac) {
					  // 방해 인자의 목표 값과 현재 값 비교
					if(targetData.getInflist().get(n).getTargetValue() < targetData.getInflist().get(n).getValue()) {
						fac.setAvailable(true);
						num++;
					}
					break;
				}
			}
		}
		
		
		if(num == 0) {
			//System.out.println(targetData.getCombination());
			if(!targetData.getCombination().isEmpty()) targetDataList.add(targetData);
			return;
		}
		// 사용 가능한 시설에 대해 처리
		for(Iterator<AvailableFacilityDto> it = copiedPostlist.iterator(); it.hasNext();) {
			AvailableFacilityDto fac = it.next();
			if(fac.isAvailable() == false) continue;
			float efficiency = 0;
			int used = 0;

			/* count facility used number */
			// 현재 타겟 데이터에서 시설의 사용 횟수 계산
			if(targetData.getCombination() != null) {
				for(FacilityDto.FindCombination tmp: targetData.getCombination()) {
					if(tmp.getFac_idx() == fac.getFac_idx()) used++;
				}
			}
			if(used >= 3) continue; //같은 시설 3회 초과 중복 방지
			
			/* run */
			//deepcopy targetData
			TargetFactorList newTargetData = targetData.deepCopy();
			//use facility
			//시설의 모든 제거 물질과 효율
			List<FacilityEfficiency.InfEff> effList = odorProofSystemMapper.selectInfEff(fac.getFac_idx());
			//run
			boolean efficiencyIsZero = false;
			for(InfEff eff: effList) {
				for(InterferenceFactorDto.Target inf: newTargetData.getInflist()) {
					if(inf.getIdx() == eff.getInf_idx()) {
						//get efficiency depending on 'used'
						if(used == 0) efficiency = eff.getEfficiency1();
						else if(used == 1) efficiency = eff.getEfficiency2();
						else if(used >= 2) efficiency = eff.getEfficiency3();
						if(efficiency > 0)inf.setValue(inf.getValue()*(1-efficiency));
						else efficiencyIsZero = true;
						break;
					}
				}
			}
			if(efficiencyIsZero) {
				it.remove();
				continue; //제거효율이 0이면 이 시설은 더 이상 사용x
			}
			//add to combination
			newTargetData.getCombination().add(new FindCombination(fac.getFac_idx(), 3));
			//call recursive
			postProcessSearch(target, newTargetData, copiedPostlist, facList, targetDataList);
			it.remove(); /* 우선순위 적용: facilityA(lowPriority) - facilityB(highPriority) 같은 조합은 나오지 않음*/
		}
	}

	//시설 조건 체크(방지시설,탄소흡착시설 한정으로 RemovalOdor 체크
	//시설 목록을 필터링하여 입력 데이터와 비교한 후, 실제로 사용할 수 있는 시설만을 반환
	private List<AvailableFacilityDto> conditionCheckProof(UserInputDataDto.Response inputdata, //사용자 입력 데이터
                                                           List<FacilityDto.Response> prooffacilityList //예측된 시설목록
															) {	
		
		List<AvailableFacilityDto> result = new ArrayList<>();
		result = this.conditionCheck(inputdata, prooffacilityList);
		
		//시설 적용가능한 물질 목록 불러와서 입력값이 있는지 체크
		for(Iterator<AvailableFacilityDto> it = result.iterator(); it.hasNext();) {
			AvailableFacilityDto tmp = it.next();

			 // 해당 시설의 적용 가능 물질 목록을 가져옴
			List<FacilityConditionDto.TargetFactor> tlist = odorProofSystemMapper.selectTargetFactor(tmp.getFac_idx());
			boolean flag = false;

			// 각 물질에 대해 사용자 입력 데이터와 비교
			if(tlist.size() == 0) flag = true;
			//System.out.println(tmp.getName() + " " + tmp.isAvailable());
			for(int j = 0; j < tlist.size(); j++) {
				double value = -1;
				  // 물질의 카테고리를 가져옴
				int catg = odorProofSystemMapper.selectInpCatg(tlist.get(j).getInp_idx());
				 // 입력 데이터에서 해당 카테고리의 물질 리스트를 가져옴
				for(int k = 0; k < inputdata.getInplist().get(catg-1).size(); k++) {
					if(inputdata.getInplist().get(catg-1).get(k).getIdx() == tlist.get(j).getInp_idx()) {
						value = inputdata.getInplist().get(catg-1).get(k).getValue();
						break;
					}
				}
				if(value < 0) continue;
				
				if(value > 0) {
					flag = true; break; //적용가능한 물질 하나 있음 == 사용가능
				}
				
			}
		     // 시설이 사용 가능하지 않으면 결과 리스트에서 제거
			if(!flag) {
				for(FacilityDto.Response proof: prooffacilityList) {
					if(tmp.getFac_idx() == proof.getFac_idx()) {
						proof.setHidden(true);
						break;
					}
				}
				it.remove();
			}
			else {
				   // 시설이 사용 가능한 경우, 예측된 시설 목록에서 숨김 상태를 해제
				for(FacilityDto.Response proof: prooffacilityList) {
					if(tmp.getFac_idx() == proof.getFac_idx()) {
						proof.setHidden(false);
						break;
					}
				}
			}
			//System.out.println(tmp.getName() + " " + tmp.isAvailable());
		}
		
		return result;
	}

	//주어진 입력 데이터를 기반으로 각 시설의 조건을 확인하고, 사용 가능한 시설을 필터링하여 반환
	private List<AvailableFacilityDto> conditionCheck(
            UserInputDataDto.Response inputdata,//사용자입력 데이터
            List<FacilityDto.Response> list //시설목록
														) {
		
		List<AvailableFacilityDto> result = new ArrayList<>();
		
		for(int i = 0; i < list.size(); i++) {
			FacilityDto.Response tmp = list.get(i);
			/* step 1: 체크박스 체크 여부 */
			if(!tmp.isAvailable()) continue; //체크박스 체크 안되어있으면 건너뛰기
			/* step 2: 추가설비 사용가능 여부 */
			String addName = odorProofSystemMapper.selectAddName(SearchRequiredAdditionalFeature(tmp.getFac_idx()));
			//System.out.println("addIdx = " + SearchRequiredAdditionalFeature(tmp.getFac_idx()) + ", addName = " + addName);
			boolean avail = true;
			if(addName != null) {
				boolean flag = false;
				 // 입력 데이터의 추가 설비 리스트에서 해당 추가 설비가 사용 가능한지 확인
				for(int j = 0; j < inputdata.getAddlist().size(); j++) {
					if(addName.equals(inputdata.getAddlist().get(j).getName()) && inputdata.getAddlist().get(j).isAvailable()) {
						flag = true;
					}
				}
				if(!flag) avail = false;
			}
			/* step 3: 시설조건 검사 (입력인자 조건, 방해인자 조건 db에서 불러와서 검사)
			 * 방해인자조건 가저와서 검사
			*/
			List<FacilityConditionDto.RefCondition> clist = odorProofSystemMapper.selectInfConditionByIdx(tmp.getFac_idx());
			//System.out.println(clist);
			double value = 0;
			for(int j = 0; j < clist.size(); j++) {
				value = -1;
				// 입력 데이터에서 해당 인자의 값을 확인
				for(int k = 0; k < inputdata.getInflist().size(); k++) {
					if(clist.get(j).getVar1() == inputdata.getInflist().get(k).getIdx()) {
						value = inputdata.getInflist().get(k).getValue();
						break;
					}
				}
				if(value < 0) continue;
				
				//System.out.println(value);

				switch(clist.get(j).getOp()) {
					case "==": if(value != clist.get(j).getVar2()) avail = false; break;
					case "!=": if(value == clist.get(j).getVar2()) avail = false; break;
					case "<=": if(value > clist.get(j).getVar2()) avail = false; break;
					case "<":  if(value >= clist.get(j).getVar2()) avail = false; break;
					case ">=": if(value < clist.get(j).getVar2()) avail = false; break;
					case ">":  if(value <= clist.get(j).getVar2()) avail = false; break;
				}
			}
			 // 입력 인자 조건을 가져와서 검사
			clist = odorProofSystemMapper.selectInpConditionByIdx(tmp.getFac_idx());
			for(int j = 0; j < clist.size(); j++) {
				value = -1;
				int catg = odorProofSystemMapper.selectInpCatg(clist.get(j).getVar1());
				for(int k = 0; k < inputdata.getInplist().get(catg-1).size(); k++) {
					if(inputdata.getInplist().get(catg-1).get(k).getIdx() == clist.get(j).getVar1()) {
						value = inputdata.getInplist().get(catg-1).get(k).getValue();
						break;
					}
				}
				if(value < 0) continue;

				switch(clist.get(j).getOp()) {
					case "==": if(value != clist.get(j).getVar2()) avail = false; break;
					case "!=": if(value == clist.get(j).getVar2()) avail = false; break;
					case "<=": if(value > clist.get(j).getVar2()) avail = false; break;
					case "<":  if(value >= clist.get(j).getVar2()) avail = false; break;
					case ">=": if(value < clist.get(j).getVar2()) avail = false; break;
					case ">":  if(value <= clist.get(j).getVar2()) avail = false; break;
				}
			}
			/* step 4: 시설이 제거하는 방해인자 입력값 있는지 검사 */
			List<FacilityEfficiency.InfEff> elist = odorProofSystemMapper.selectInfEff(tmp.getFac_idx());
			//System.out.println(tmp.getFac_idx() + " " + elist);
			if(!elist.isEmpty()) {
				boolean flag = false;
				for(InfEff eff: elist) {
					value = -1;
					for(InterferenceFactorDto.Response inf: inputdata.getInflist()) {
						if(inf.getIdx() == eff.getInf_idx()) {
							value = inf.getValue();
							break;
						}
					}
					if(value < 0) continue;
					
					if(value > 0) {
						flag = true; break; //적용가능한 물질 하나 있음 == 사용가능
					}
				}
				if(!flag) avail = false;
			}
			
			/* step 5: 시설이 사용가능하면 사용가능한 시설 리스트에 더하기 */
			if(avail) {
				result.add(new AvailableFacilityDto(tmp.getFac_idx(), SearchPriority(tmp.getFac_idx()), true));
				tmp.setHidden(false);
			}
			else {
				tmp.setHidden(true);
			}
		}
		return result;
	}

	// TargetFactorList 객체의 유효성을 검증
	private boolean validateCombination(TargetFactorList targetData) {
		// 이론적 희석배수 < 목표 희석배수 ?
		if(targetData.getDilutionFactor() > targetData.getTargetDilutionFactor()) return false;
		// 방해인자 값 < 목표값?
		for(Target inf: targetData.getInflist()) {
			 // 사용 가능한 방해인자를 데이터베이스에서 조회하고, 현재 값이 목표 값보다 큰지 확인
			if(odorProofSystemMapper.selectUsableFacInf(inf.getIdx()) != null && inf.getValue() > inf.getTargetValue()) return false;
		}
		return true;
	}
	//시설 ID를 기반으로 추가 기능 요구 사항을 검색
	private int SearchRequiredAdditionalFeature(int idx) {
		if(facilityList == null) return 0;
		for(int i = 0; i < facilityList.size(); i++) {
			if(idx == facilityList.get(i).getFac_idx()) return facilityList.get(i).getRequiredAdditionalFeature();
		}
		return 0;
	}
	//시설 ID를 기반으로 시설 이름을 검색
	private String SearchName(int idx) {
		if(facilityList == null) return null;
		for(int i = 0; i < facilityList.size(); i++) {
			if(idx == facilityList.get(i).getFac_idx()) return facilityList.get(i).getName();
		}
		return null;
	}
	//시설 이미지 소스 검색
	private String SearchImagesrc(int idx) {
		if(facilityList == null) return null;
		for(int i = 0; i < facilityList.size(); i++) {
			if(idx == facilityList.get(i).getFac_idx()) return facilityList.get(i).getImagesrc();
		}
		return null;
	}
	//우선순위 검색
	private int SearchPriority(int idx) {
		if(facilityList == null) return 0;
		for(int i = 0; i < facilityList.size(); i++) {
			if(idx == facilityList.get(i).getFac_idx()) return facilityList.get(i).getPriority();
		}
		return 0;
	}
   //이미지 및 시설 상세 정보 불러오기
   public List<FacilityDto.CombinationResponse> getImageAndFacs(int i) {
	   // 선택된 조합에서 시설 정보 가져오기
	   return comList.get(i).getCombination();
   }
    // 해당조합 불러오기
	@Override
	public FacilityCombination getCombination(int i) {
		return comList.get(i);
	}


    //주어진 시설 응답 데이터(facility)를 시설 요청 데이터(facilityreq)로 업데이트
	@Override
	public void saveData(Response facility, Request facilityreq) {
		if(facilityreq.getPrefacilityList() != null) {
			for(int i = 0; i < facilityreq.getPrefacilityList().size(); i++) {
				if(!facility.getPrefacilityList().get(i).isHidden()) facility.getPrefacilityList().get(i).setAvailable(facilityreq.getPrefacilityList().get(i).isAvailable());
			}
		}
		if(facilityreq.getProoffacilityList() != null) {
			for(int i = 0; i < facilityreq.getProoffacilityList().size(); i++) {
				if(!facility.getProoffacilityList().get(i).isHidden()) facility.getProoffacilityList().get(i).setAvailable(facilityreq.getProoffacilityList().get(i).isAvailable());
			}
		}
		if(facilityreq.getPostfacilityList() != null) {
			for(int i = 0; i < facilityreq.getPostfacilityList().size(); i++) {
				if(!facility.getPostfacilityList().get(i).isHidden()) facility.getPostfacilityList().get(i).setAvailable(facilityreq.getPostfacilityList().get(i).isAvailable());
			}
		}
		if(facilityreq.getCarbonfacilityList() != null) {
			for(int i = 0; i < facilityreq.getCarbonfacilityList().size(); i++) {
				if(!facility.getCarbonfacilityList().get(i).isHidden()) facility.getCarbonfacilityList().get(i).setAvailable(facilityreq.getCarbonfacilityList().get(i).isAvailable());
			}
		}
		return;
	}

	
	

}
