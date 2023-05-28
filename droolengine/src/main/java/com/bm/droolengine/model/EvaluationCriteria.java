package com.bm.droolengine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationCriteria {
    private String sellerId;
    private Double department;
    private String purchagedLocation;
    private String action;
}
