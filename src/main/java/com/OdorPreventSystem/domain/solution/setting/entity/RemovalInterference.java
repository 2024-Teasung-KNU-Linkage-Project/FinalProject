package com.OdorPreventSystem.domain.solution.setting.entity;

import com.OdorPreventSystem.domain.solution.entity.InterferenceFactor;
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
public class RemovalInterference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final int idx;

    @ManyToOne
    @JoinColumn(name = "efficiency")
    private final RemovalEfficiency efficiency;

    @ManyToOne
    @JoinColumn(name = "interference")
    private final InterferenceFactor interference;

}
