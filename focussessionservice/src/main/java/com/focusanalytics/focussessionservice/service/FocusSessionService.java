package com.focusanalytics.focussessionservice.service;

import com.focusanalytics.focussessionservice.FocusSessionRepository;
import com.focusanalytics.focussessionservice.dto.FocusSessionRequest;
import com.focusanalytics.focussessionservice.dto.FocusSessionResponse;
import com.focusanalytics.focussessionservice.model.FocusSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, FocusSession> kafkaTemplate;

    @Value("${kafka.topic.name:focus-session-events}")
    private String topicName;

    public FocusSessionResponse trackFocusSession(FocusSessionRequest request) {

        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if (!isValidUser) {
            throw new IllegalArgumentException("Invalid focus-session user: " + request.getUserId());
        }

        FocusSession focusSession = FocusSession.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .productivityScore(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .taskMetadata(request.getAdditionalMetrics())
                .build();

        FocusSession savedFocusSession = focusSessionRepository.save(focusSession);

        try {
            kafkaTemplate.send(topicName, savedFocusSession.getUserId(), savedFocusSession);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish focus session event", e);
        }


        return mapToResponse(savedFocusSession);
    }

    private FocusSessionResponse mapToResponse(FocusSession focusSession) {
        FocusSessionResponse response = new FocusSessionResponse();
        response.setId(focusSession.getId());
        response.setUserId(focusSession.getUserId());
        response.setType(focusSession.getType());
        response.setDuration(focusSession.getDuration());
        response.setCaloriesBurned(focusSession.getProductivityScore());
        response.setStartTime(focusSession.getStartTime());
        response.setAdditionalMetrics(focusSession.getTaskMetadata());
        response.setCreatedAt(focusSession.getCreatedAt());
        response.setUpdatedAt(focusSession.getUpdatedAt());
        return response;

    }


    public List<FocusSessionResponse> getUserFocusSessions(String userId) {
        List<FocusSession> focusSessionList = focusSessionRepository.findByUserId(userId);
        return focusSessionList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

}


