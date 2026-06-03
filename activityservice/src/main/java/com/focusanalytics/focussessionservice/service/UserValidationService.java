package com.focusanalytics.focussessionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info("Calling profile service for {}", userId);
        try {
            log.debug("Making WebClient call to validate user: {}", userId);
            boolean result = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            log.info("Validation successful for {}: {}", userId, result);

            return result;
        } catch (WebClientResponseException e) {
            log.warn("User validation failed for {}", userId, e);
        }catch (WebClientRequestException e) {
            log.error("Connection Error validating user {}: {}", userId, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error validating user {}: {}", userId, e.getMessage(), e);
        }
        return false;
    }
}


