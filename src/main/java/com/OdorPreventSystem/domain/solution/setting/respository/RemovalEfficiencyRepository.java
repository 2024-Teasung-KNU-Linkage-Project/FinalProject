package com.OdorPreventSystem.domain.solution.setting.respository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.setting.entity.RemovalEfficiency;

public interface RemovalEfficiencyRepository
        extends CrudRepository<RemovalEfficiency, Integer> {
}
