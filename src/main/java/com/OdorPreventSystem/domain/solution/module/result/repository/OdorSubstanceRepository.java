package com.OdorPreventSystem.domain.solution.module.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;

public interface OdorSubstanceRepository
        extends CrudRepository<OdorSubstance, Integer> {
}
