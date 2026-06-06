package com.focusanalytics.insightservice.service;

import com.focusanalytics.insightservice.model.Insight;
import com.focusanalytics.insightservice.respository.InsightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    private InsightRepository insightRepository;

    @InjectMocks
    private InsightService insightService;

    @Test
    void getUserInsightsReturnsRepositoryResults() {
        Insight insight = Insight.builder().id("insight-1").userId("user-1").build();
        when(insightRepository.findByUserId("user-1")).thenReturn(List.of(insight));

        List<Insight> insights = insightService.getUserInsights("user-1");

        assertEquals(1, insights.size());
        assertEquals("insight-1", insights.get(0).getId());
    }

    @Test
    void getFocusSessionInsightReturnsMatch() {
        Insight insight = Insight.builder().id("insight-2").focusSessionId("session-1").build();
        when(insightRepository.findByFocusSessionId("session-1")).thenReturn(Optional.of(insight));

        Insight result = insightService.getFocusSessionInsight("session-1");

        assertEquals("insight-2", result.getId());
    }

    @Test
    void getFocusSessionInsightThrowsWhenMissing() {
        when(insightRepository.findByFocusSessionId("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> insightService.getFocusSessionInsight("missing"));

        assertTrue(ex.getMessage().contains("missing"));
    }
}
