package com.focusanalytics.insightservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.focusanalytics.insightservice.model.FocusSession;
import com.focusanalytics.insightservice.model.Insight;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class InsightAIService {
    private final GeminiService geminiService;

    public Insight generateInsight(FocusSession focusSession) {
        String prompt = createPromptForFocusSession(focusSession);
        String aiResponse = geminiService.getInsights(prompt);
        log.info("RESPONSE FROM AI {} ", aiResponse);
        return processAIResponse(focusSession, aiResponse);
    }

    private Insight processAIResponse(FocusSession focusSession, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

//            log.info("RESPONSE FROM CLEANED AI {} ", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "focusScore", "Focus Score:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Insight.builder()
                    .focusSessionId(focusSession.getId())
                    .userId(focusSession.getUserId())
                    .type(focusSession.getType().toString())
                    .insight(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.warn("Falling back to default insight generation", e);
            return createDefaultInsight(focusSession);
        }
    }

    private Insight createDefaultInsight(FocusSession focusSession) {
        return Insight.builder()
                .focusSessionId(focusSession.getId())
                .userId(focusSession.getUserId())
                .type(focusSession.getType().toString())
                .insight("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider adjusting your work blocks"))
                .safety(Arrays.asList(
                        "Take regular breaks",
                        "Stay hydrated",
                        "Avoid long uninterrupted screen time"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions provided") :
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                improvements;

    }

    //    "overall": "This was an excellent"
    // Overall: This was an excellent
    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForFocusSession(FocusSession focusSession) {
        return String.format("""
        Analyze this focus session and provide detailed productivity insights in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "focusScore": "Productivity score analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Work block name",
              "description": "Detailed next-step description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this focus session:
        Session Type: %s
        Duration: %d minutes
        Productivity Score: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on productivity, improvements, next-step suggestions, and sustainable focus guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                focusSession.getType(),
                focusSession.getDuration(),
                focusSession.getFocusScore(),
                focusSession.getTaskMetadata()
        );
    }
}


