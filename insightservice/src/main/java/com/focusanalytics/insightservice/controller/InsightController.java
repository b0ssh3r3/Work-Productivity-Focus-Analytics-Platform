package com.focusanalytics.insightservice.controller;

import com.focusanalytics.insightservice.model.Insight;
import com.focusanalytics.insightservice.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/insights")
public class InsightController {
    private final InsightService insightService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Insight>> getUserInsights(@PathVariable String userId) {
        return ResponseEntity.ok(insightService.getUserInsights(userId));
    }

    @GetMapping("/focus-session/{focusSessionId}")
    public ResponseEntity<Insight> getFocusSessionInsight(@PathVariable String focusSessionId) {
        return ResponseEntity.ok(insightService.getFocusSessionInsight(focusSessionId));
    }
}


