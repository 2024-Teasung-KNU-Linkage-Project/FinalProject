package com.OdorPreventSystem.domain.solution.company.repository;

import com.OdorPreventSystem.domain.solution.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByNameContaining(String name);
}
