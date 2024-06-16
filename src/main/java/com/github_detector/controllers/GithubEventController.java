package com.github_detector.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github_detector.common.GithubActionType;
import com.github_detector.common.GithubEventData;
import com.github_detector.common.GithubEventType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class GithubEventController {


    public static final String EVENT_TYPE_HEADER = "X-GitHub-Event";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final int timeDifferenceFromGithhub;

    private final  ObjectMapper objectMapper = new ObjectMapper();

    public GithubEventController(@Value("${github-time-diff}") int timeDifferenceFromGithhub) {
        this.timeDifferenceFromGithhub = timeDifferenceFromGithhub;
    }


    @PostMapping("/test")
    public ResponseEntity<String> handleGithubEvent(
            @RequestHeader(EVENT_TYPE_HEADER) String eventType,
            @RequestBody String eventPayload) {
        try {

            JsonNode payloadJsonNode;

            payloadJsonNode = objectMapper.readTree(eventPayload);

            GithubEventData eventData = extractEventDataFromPayload(payloadJsonNode, eventType);

            return null;
        }
         catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON payload");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("An unexpected Error occurred");
        }

    }

    private GithubEventData extractEventDataFromPayload(JsonNode jsonNode, String eventType){
        GithubEventData eventData = new GithubEventData();

        eventData.setGithubEventType(GithubEventType.getFromString(eventType));

        eventData.setEventTimestamp(LocalDateTime.now().minusHours(timeDifferenceFromGithhub));

        if (jsonNode.has("action")) {
            String action = jsonNode.path("action").asText();
            eventData.setAction(GithubActionType.getFromString(action));
        }

        if (jsonNode.has("team")) {
            String teamName = jsonNode.path("team").path("name").asText();
            eventData.setTeamName(teamName);
        }
        if (jsonNode.has("repository")) {
            String repositoryName = jsonNode.path("repository").path("name").asText();
            LocalDateTime repositoryCreatedAt = convertTimestampString(jsonNode.path("repository").path("created_at").asText());
            eventData.setRepositoryCreatedAt(repositoryCreatedAt);

            eventData.setRepositoryName(repositoryName);
            eventData.setRepositoryCreatedAt(repositoryCreatedAt);
        }
        return eventData;
    }

    private LocalDateTime convertTimestampString(String timestamp){
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}
