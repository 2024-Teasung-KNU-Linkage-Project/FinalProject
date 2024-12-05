package com.OdorPreventSystem.domain.solution.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED, force=true)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer idx;

    private String name;
    private double theoreticalDillutionFactor;
    private double targetDillutionFactor;
    private double expectedDillutionFactor;
    private double wind;
    private final Date storedAt = new Date();

}
