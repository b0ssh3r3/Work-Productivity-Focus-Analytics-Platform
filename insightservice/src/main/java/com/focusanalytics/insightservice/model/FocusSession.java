package com.focusanalytics.insightservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FocusSession {
    private String id;
    private String userId;
    private FocusSessionType type;
    private Integer duration;
    private Integer productivityScore;
    private LocalDateTime startTime;

    @Field("taskMetadata")
    private Map<String, Object> taskMetadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


