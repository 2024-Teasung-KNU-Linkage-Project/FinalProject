package com.OdorPreventSystem.domain.solution.module.result.repository;

import org.springframework.data.repository.CrudRepository;

import com.OdorPreventSystem.domain.solution.entity.Result;

public interface ResultRepository
        extends CrudRepository<Result, Integer> {
}
