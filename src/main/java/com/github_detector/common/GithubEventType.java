package com.github_detector.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum GithubEventType {
    PUSH("push"),
    REPOSITORY("repository"),
    TEAM("team"),
    OTHER("other");

    private static final Map<String,GithubEventType> EVENT_TO_ENUM_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(GithubEventType::getEventName, event -> event));

    private final String eventName;


    GithubEventType(String eventName){
        this.eventName = eventName;
    }

    public static GithubEventType getFromString(String eventName){
        return EVENT_TO_ENUM_MAP.getOrDefault(eventName, OTHER);
    }
}
