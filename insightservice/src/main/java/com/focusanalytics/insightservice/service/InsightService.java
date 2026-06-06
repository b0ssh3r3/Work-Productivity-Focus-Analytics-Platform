package com.focusanalytics.insightservice.service;

import com.focusanalytics.insightservice.model.Insight;
import com.focusanalytics.insightservice.respository.InsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsightService {
    private final InsightRepository insightRepository;

    public List<Insight> getUserInsights(String userId) {
        return insightRepository.findByUserId(userId);
    }

    public Insight getFocusSessionInsight(String focusSessionId) {
        return insightRepository.findByFocusSessionId(focusSessionId)
                .orElseThrow(() -> new RuntimeException("No insight found for this focus session: " + focusSessionId));
    }
}


