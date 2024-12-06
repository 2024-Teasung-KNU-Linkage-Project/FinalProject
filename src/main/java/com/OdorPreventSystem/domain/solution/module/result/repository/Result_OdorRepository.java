package com.OdorPreventSystem.domain.solution.module.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.Result_Odor;

public interface Result_OdorRepository
        extends CrudRepository<Result_Odor, Integer> {
}
