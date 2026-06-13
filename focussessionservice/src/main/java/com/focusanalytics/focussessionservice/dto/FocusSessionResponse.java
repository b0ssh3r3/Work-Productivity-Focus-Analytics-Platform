package com.focusanalytics.focussessionservice.dto;

import com.focusanalytics.focussessionservice.model.FocusSessionType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FocusSessionResponse {
    private String id;
    private String userId;
    private FocusSessionType type;
    private Integer duration;
    private Integer focusScore;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


