package com.OdorPreventSystem.domain.solution.module.company.entity;

import com.OdorPreventSystem.domain.solution.module.search.PlaceDataDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED, force=true)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String name;
    private final String outlet;
    private final String city;
    private final String addr;
    private final float lat;
    private final float lon;
    private final String csv_filename;
    private final String odor;

    public PlaceDataDTO toPlaceDataDTO(){
        return PlaceDataDTO.builder()
                .companyIndex(this.id)
                .name(this.name)
                .outlet(this.outlet)
                .latitude(this.lat)
                .longitude(this.lon)
                .odor(this.odor)
                .csvFilename(this.csv_filename)
                .build();
    }
}
