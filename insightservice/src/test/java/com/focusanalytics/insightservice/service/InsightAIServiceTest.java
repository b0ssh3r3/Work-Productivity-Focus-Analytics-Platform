package com.focusanalytics.insightservice.service;

import com.focusanalytics.insightservice.model.FocusSession;
import com.focusanalytics.insightservice.model.FocusSessionType;
import com.focusanalytics.insightservice.model.Insight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightAIServiceTest {

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private InsightAIService insightAIService;

    @Test
    void generateInsightParsesGeminiResponse() {
        FocusSession focusSession = FocusSession.builder()
                .id("session-1")
                .userId("user-1")
                .type(FocusSessionType.OTHER)
                .duration(40)
                .focusScore(77)
                .startTime(LocalDateTime.of(2026, 6, 1, 9, 0))
                .taskMetadata(Map.of("notes", "ship feature"))
                .build();

        String aiResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "```json\\n{\\"analysis\\":{\\"overall\\":\\"Good session\\",\\"pace\\":\\"Steady\\",\\"heartRate\\":\\"Stable\\",\\"focusScore\\":\\"Strong\\"},\\"improvements\\":[{\\"area\\":\\"Focus\\",\\"recommendation\\":\\"Reduce context switching\\"}],\\"suggestions\\":[{\\"workout\\":\\"Next block\\",\\"description\\":\\"Plan a deep work block\\"}],\\"safety\\":[\\"Take breaks\\"]}\\n```"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        when(geminiService.getInsights(org.mockito.ArgumentMatchers.anyString())).thenReturn(aiResponse);

        Insight insight = insightAIService.generateInsight(focusSession);

        assertEquals("session-1", insight.getFocusSessionId());
        assertEquals("user-1", insight.getUserId());
        assertTrue(insight.getInsight().contains("Overall:"));
        assertEquals(1, insight.getImprovements().size());
        assertEquals(1, insight.getSuggestions().size());
        assertEquals(1, insight.getSafety().size());
    }
}
