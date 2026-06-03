package com.focusanalytics.focussessionservice.controller;

import com.focusanalytics.focussessionservice.dto.FocusSessionRequest;
import com.focusanalytics.focussessionservice.dto.FocusSessionResponse;
import com.focusanalytics.focussessionservice.service.FocusSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/focus-sessions")
@AllArgsConstructor
public class FocusSessionController {

    private FocusSessionService focusSessionService;

    @PostMapping
    public ResponseEntity<FocusSessionResponse> trackFocusSession(@RequestBody FocusSessionRequest request, @RequestHeader("X-User-ID") String userId) {
        request.setUserId(userId);
        return ResponseEntity.ok(focusSessionService.trackFocusSession(request));
    }


    @GetMapping
    public ResponseEntity<List<FocusSessionResponse>> getUserFocusSessions(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(focusSessionService.getUserFocusSessions(userId));
    }
}


