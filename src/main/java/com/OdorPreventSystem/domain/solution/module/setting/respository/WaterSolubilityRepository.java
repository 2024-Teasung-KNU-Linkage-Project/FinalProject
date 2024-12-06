package com.OdorPreventSystem.domain.solution.module.setting.respository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.WaterSolubility;

public interface WaterSolubilityRepository
        extends CrudRepository<WaterSolubility, Integer> {
}
