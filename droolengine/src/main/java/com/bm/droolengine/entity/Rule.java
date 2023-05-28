package com.bm.droolengine.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Rule {
    private String id;
    private String name;
    private String type;
    private String effectiveStartDate;
    private String effectiveEndDate;
    private String status;
    private String expression;
    private String action;
    private Integer priority;
}
