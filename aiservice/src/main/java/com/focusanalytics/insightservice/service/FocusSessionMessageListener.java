package com.focusanalytics.insightservice.service;

import com.focusanalytics.insightservice.model.FocusSession;
import com.focusanalytics.insightservice.model.Insight;
import com.focusanalytics.insightservice.respository.InsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FocusSessionMessageListener {

    private final InsightAIService insightAIService;
    private final InsightRepository insightRepository;

    @KafkaListener(topics = "${kafka.topic.name:focus-session-events}", groupId = "focus-session-processor-group")
    public void processActivity(FocusSession focusSession) {
        log.info("Received Focus Session for processing: {}", focusSession.getUserId());
        Insight insight = insightAIService.generateInsight(focusSession);
        insightRepository.save(insight);
    }
}


