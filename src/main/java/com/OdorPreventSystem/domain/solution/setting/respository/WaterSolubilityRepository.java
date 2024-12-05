package com.OdorPreventSystem.domain.solution.setting.respository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.setting.entity.WaterSolubility;

public interface WaterSolubilityRepository
        extends CrudRepository<WaterSolubility, Integer> {
}
