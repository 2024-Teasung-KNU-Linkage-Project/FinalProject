package com.OdorPreventSystem.domain.solution.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED, force=true)
public class OdorSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final int idx;

    private final String name;

    @ManyToOne
    @JoinColumn(name="category")
    private final OdorCategory category;

    private final double threshold;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="waterSolubility")
    private final WaterSolubility waterSolubility;


    private final  String name_en;

}
