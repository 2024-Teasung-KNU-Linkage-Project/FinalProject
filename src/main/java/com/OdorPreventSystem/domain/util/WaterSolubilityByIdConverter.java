package com.OdorPreventSystem.domain.util;

import com.OdorPreventSystem.domain.solution.module.setting.respository.WaterSolubilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.OdorPreventSystem.domain.solution.module.setting.dto.WaterSolubilityRequest;

@Component
public class WaterSolubilityByIdConverter implements Converter<String, WaterSolubilityRequest> {

    private WaterSolubilityRepository waterSolubilityRepo;

    @Autowired
    // WaterSolubilityByIdConverter 클래스의 인스턴스를 초기화
    // WaterSolubilityRepository 객체를 주입받아 클래스의 필드에 할당
    public WaterSolubilityByIdConverter(WaterSolubilityRepository waterSolubilityRepo) {
        this.waterSolubilityRepo = waterSolubilityRepo;
    }

    @Override
    // 문자열 형태의 ID를 WaterSolubilityRequest 객체로 변환하는 역할
    public WaterSolubilityRequest convert(String id) {
        return new WaterSolubilityRequest(Integer.valueOf(id));
    }

}
