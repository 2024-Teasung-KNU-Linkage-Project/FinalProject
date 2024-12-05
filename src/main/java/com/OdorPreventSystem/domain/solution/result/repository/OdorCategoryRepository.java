package com.OdorPreventSystem.domain.solution.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.OdorCategory;

public interface OdorCategoryRepository
        extends CrudRepository<OdorCategory, Integer> {
}
