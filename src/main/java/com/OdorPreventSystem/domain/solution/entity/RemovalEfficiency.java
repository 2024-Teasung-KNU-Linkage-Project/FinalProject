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
public class RemovalEfficiency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final int idx;

    @ManyToOne
    @JoinColumn(name = "facility")
    private final OdorPreventionFacility facility;

    private final float efficiency1;
    private final float efficiency2;
    private final float efficiency3;

}
