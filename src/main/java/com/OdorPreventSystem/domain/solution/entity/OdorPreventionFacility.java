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

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED, force=true)
public class OdorPreventionFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final int idx;

    private final String name;

    @ManyToOne
    @JoinColumn(name = "category")
    private final FacilityCategory category;

    @ManyToOne
    @JoinColumn(name = "requiredAdditional")
    private final AdditionalFeature requiredAdditional;

    private final int priority;
    private final int basePrice;
    private final long priceByWind;
    private final String image;

}
