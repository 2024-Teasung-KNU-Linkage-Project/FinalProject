package com.OdorPreventSystem.domain.solution.module.setting.respository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.RemovalInterference;

public interface RemovalInterferenceRepository
        extends CrudRepository<RemovalInterference, Integer> {
}
