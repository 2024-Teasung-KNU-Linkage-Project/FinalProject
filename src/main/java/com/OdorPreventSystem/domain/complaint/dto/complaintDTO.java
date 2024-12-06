package com.OdorPreventSystem.domain.complaint.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class complaintDTO {
    private Long complaintId;
    private String userName;
    private String context;
    private String provinceCityName;
    private Integer postalCode;
    private String streetAddress;
    private Double latitude;
    private Double longitude;
    private float windDirection;
    private float windSpeed;
    private Integer odor;
    private String odorIntensity;
    private LocalDateTime date;
    private String odorName;
}
