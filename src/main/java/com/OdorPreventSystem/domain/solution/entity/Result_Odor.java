package com.OdorPreventSystem.domain.solution.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED, force=true)
public class Result_Odor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer idx;

    @ManyToOne
    @JoinColumn(name = "result")
    private Result result;

    @ManyToOne
    @JoinColumn(name = "odor")
    private OdorSubstance odor;

    private double value;
    private double expectedValue;

}
