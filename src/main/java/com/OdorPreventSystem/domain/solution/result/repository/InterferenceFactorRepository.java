package com.OdorPreventSystem.domain.solution.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.InterferenceFactor;

public interface InterferenceFactorRepository
        extends CrudRepository<InterferenceFactor, Integer> {
}
