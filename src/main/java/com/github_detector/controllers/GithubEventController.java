package com.github_detector.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github_detector.common.GithubActionType;
import com.github_detector.common.GithubEventData;
import com.github_detector.common.GithubEventType;
import com.github_detector.services.GithubEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
public class GithubEventController {


    public static final String EVENT_TYPE_HEADER = "X-GitHub-Event";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String INVALID_JSON_PAYLOAD = "Invalid JSON payload";
    private static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String EVENT_PROCESSING_SUCCESS = "Github event processed";
    public static final String INVALID_TIMESTAMP_FORMAT = "Invalid timestamp format: ";


    private final int timeDifferenceFromGithhub;
    private final GithubEventService githubEventService;
    private final  ObjectMapper objectMapper = new ObjectMapper();

    public GithubEventController(@Value("${github-.hours-diff}") int timeDifferenceFromGithhub,
                                 @Autowired GithubEventService githubEventService) {
        this.timeDifferenceFromGithhub = timeDifferenceFromGithhub;
        this.githubEventService = githubEventService;
    }


    @PostMapping("/github-event")
    public ResponseEntity<String> handleGithubEvent(
            @RequestHeader(EVENT_TYPE_HEADER) String eventType,
            @RequestBody String eventPayload) {
        try {

            JsonNode payloadJsonNode;

            payloadJsonNode = objectMapper.readTree(eventPayload);

            GithubEventData eventData = extractEventDataFromPayload(payloadJsonNode, eventType);
            githubEventService.processGithubEvent(eventData);
            return ResponseEntity.status(HttpStatus.OK).body(EVENT_PROCESSING_SUCCESS);
        }
         catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(INVALID_JSON_PAYLOAD);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(UNEXPECTED_ERROR);
        }

    }

    private GithubEventData extractEventDataFromPayload(JsonNode jsonNode, String eventType){
        GithubEventData eventData = new GithubEventData();

        eventData.setGithubEventType(GithubEventType.getFromString(eventType));
        eventData.setEventTimestamp(LocalDateTime.now().minusHours(timeDifferenceFromGithhub));

        if (jsonNode.has("action")) {
            String action = jsonNode.path("action").asText();
            eventData.setGithubActionType(GithubActionType.getFromString(action));
        }

        if (jsonNode.has("team")) {
            String teamName = jsonNode.path("team").path("name").asText();
            eventData.setTeamName(teamName);
        }
        if (jsonNode.has("repository")) {
            String repositoryName = jsonNode.path("repository").path("name").asText();
            eventData.setRepositoryName(repositoryName);

            LocalDateTime repositoryCreatedAt = convertTimestampString(jsonNode.path("repository").path("created_at").asText());
            eventData.setRepositoryCreatedAt(repositoryCreatedAt);
        }
        return eventData;
    }

    private  LocalDateTime convertTimestampString(String timestamp) {
        try {
            long unixTimestamp = Long.parseLong(timestamp);
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),ZoneOffset.UTC);
        } catch (NumberFormatException e) { //timestamp has a different format depending on the event type
            try {
                return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
            } catch (DateTimeParseException dateTimeParseException) {
                throw new IllegalArgumentException(INVALID_TIMESTAMP_FORMAT + timestamp, dateTimeParseException);
            }
        }
    }
}
