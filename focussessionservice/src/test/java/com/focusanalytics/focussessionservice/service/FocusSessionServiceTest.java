package com.focusanalytics.focussessionservice.service;

import com.focusanalytics.focussessionservice.FocusSessionRepository;
import com.focusanalytics.focussessionservice.dto.FocusSessionRequest;
import com.focusanalytics.focussessionservice.dto.FocusSessionResponse;
import com.focusanalytics.focussessionservice.model.FocusSession;
import com.focusanalytics.focussessionservice.model.FocusSessionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FocusSessionServiceTest {

    @Mock
    private FocusSessionRepository focusSessionRepository;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private KafkaTemplate<String, FocusSession> kafkaTemplate;

    @InjectMocks
    private FocusSessionService focusSessionService;

    @Test
    void trackFocusSessionMapsPersistenceFieldsAndPublishesEvent() {
        FocusSessionRequest request = new FocusSessionRequest();
        request.setUserId("user-1");
        request.setType(FocusSessionType.OTHER);
        request.setDuration(45);
        request.setCaloriesBurned(82);
        request.setStartTime(LocalDateTime.of(2026, 6, 1, 10, 0));
        request.setAdditionalMetrics(Collections.singletonMap("notes", "refactor #backend"));

        FocusSession saved = FocusSession.builder()
                .id("session-1")
                .userId("user-1")
                .type(FocusSessionType.OTHER)
                .duration(45)
                .productivityScore(82)
                .startTime(request.getStartTime())
                .taskMetadata(request.getAdditionalMetrics())
                .createdAt(LocalDateTime.of(2026, 6, 1, 10, 45))
                .updatedAt(LocalDateTime.of(2026, 6, 1, 10, 45))
                .build();

        when(userValidationService.validateUser("user-1")).thenReturn(true);
        when(focusSessionRepository.save(any(FocusSession.class))).thenReturn(saved);

        FocusSessionResponse response = focusSessionService.trackFocusSession(request);

        ArgumentCaptor<FocusSession> captor = ArgumentCaptor.forClass(FocusSession.class);
        verify(focusSessionRepository).save(captor.capture());
        FocusSession persisted = captor.getValue();

        assertEquals("user-1", persisted.getUserId());
        assertEquals(FocusSessionType.OTHER, persisted.getType());
        assertEquals(82, persisted.getProductivityScore());
        assertEquals(request.getAdditionalMetrics(), persisted.getTaskMetadata());
        assertEquals("session-1", response.getId());
        assertEquals(82, response.getCaloriesBurned());
        verify(kafkaTemplate).send(anyString(), eq("user-1"), eq(saved));
    }

    @Test
    void getUserFocusSessionsMapsStoredSessions() {
        FocusSession session = FocusSession.builder()
                .id("session-2")
                .userId("user-1")
                .type(FocusSessionType.WALKING)
                .duration(30)
                .productivityScore(64)
                .build();

        when(focusSessionRepository.findByUserId("user-1")).thenReturn(List.of(session));

        List<FocusSessionResponse> responses = focusSessionService.getUserFocusSessions("user-1");

        assertEquals(1, responses.size());
        assertEquals("session-2", responses.get(0).getId());
        assertEquals(64, responses.get(0).getCaloriesBurned());
    }

    @Test
    void trackFocusSessionRejectsInvalidUser() {
        FocusSessionRequest request = new FocusSessionRequest();
        request.setUserId("user-1");

        when(userValidationService.validateUser("user-1")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> focusSessionService.trackFocusSession(request));
        verifyNoInteractions(focusSessionRepository, kafkaTemplate);
    }
}
