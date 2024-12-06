package com.OdorPreventSystem.domain.complaint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class complaintLocationDateDTO {
    private Long complaintId;
    private Double latitude;
    private Double longitude;
    private String date;
}