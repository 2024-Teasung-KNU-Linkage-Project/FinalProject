package com.OdorPreventSystem.domain.solution.module.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.AdditionalFeature;

public interface AdditionalFeatureRepository
        extends CrudRepository<AdditionalFeature, Integer> {
}
