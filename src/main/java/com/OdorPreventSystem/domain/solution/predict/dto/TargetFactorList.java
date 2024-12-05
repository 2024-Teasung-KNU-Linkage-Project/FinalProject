package com.OdorPreventSystem.domain.solution.predict.dto;

import java.util.ArrayList;
import java.util.List;

import com.OdorPreventSystem.domain.solution.inputFactor.dto.InputFactorDto;
import com.OdorPreventSystem.domain.solution.inputFactor.dto.InterferenceFactorDto;

import lombok.Data;


//검사해야할 입력인자, 방해인자, 이론적 희석배수(반올림 x), 목표 희석배수
@Data
public class TargetFactorList {
	private List<InputFactorDto.Target> inplist; //입력인자 리스트
	private List<InterferenceFactorDto.Target> inflist; //방해인자 리스트
	private double dilutionFactor; //이론적 희석배수
	private int targetDilutionFactor; //목표 희석배수
	private List<FacilityDto.FindCombination> combination; //시설클론한 조합리스트

	public TargetFactorList deepCopy() {
		//새로운 객체 생성
		TargetFactorList newT = new TargetFactorList();
		List<InputFactorDto.Target> newinp = new ArrayList<>(); 
		List<InterferenceFactorDto.Target> newinf = new ArrayList<>();
		List<FacilityDto.FindCombination> newcom = new ArrayList<>();

		//원본객체의 값 복제
		newT.setDilutionFactor(this.getDilutionFactor());
		newT.setTargetDilutionFactor(this.getTargetDilutionFactor());
		

		//원본객체 요소 복사 
		for(InputFactorDto.Target inp: this.getInplist()) {
			try {
				newinp.add(inp.clone());
			} catch (CloneNotSupportedException e) {
				
				e.printStackTrace();
			}
		}

		for(InterferenceFactorDto.Target inf: this.getInflist()) {
			try {
				newinf.add(inf.clone());
			} catch (CloneNotSupportedException e) {
				
				e.printStackTrace();
			}
		}

		for(FacilityDto.FindCombination c: this.getCombination()) {
			try {
				newcom.add(c.clone());
			} catch (CloneNotSupportedException e) {
	
				e.printStackTrace();
			}
		}
		//새로운 list 새 객체에 설성
		newT.setInplist(newinp);
		newT.setInflist(newinf);
		newT.setCombination(newcom);
		
		return newT;
	}

}