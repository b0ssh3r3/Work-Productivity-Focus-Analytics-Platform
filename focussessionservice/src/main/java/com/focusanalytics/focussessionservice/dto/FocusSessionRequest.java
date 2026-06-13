package com.focusanalytics.focussessionservice.dto;

import com.focusanalytics.focussessionservice.model.FocusSessionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FocusSessionRequest {
    private String userId;
    private FocusSessionType type;
    private Integer duration;
    private Integer focusScore;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
}


