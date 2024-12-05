package com.OdorPreventSystem.domain.solution.setting.respository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.setting.entity.RemovalInterference;

public interface RemovalInterferenceRepository
        extends CrudRepository<RemovalInterference, Integer> {
}
