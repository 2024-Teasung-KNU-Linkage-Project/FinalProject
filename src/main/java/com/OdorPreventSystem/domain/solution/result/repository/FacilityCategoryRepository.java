package com.OdorPreventSystem.domain.solution.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.FacilityCategory;

public interface FacilityCategoryRepository
        extends CrudRepository<FacilityCategory, Integer> {
}
