package com.github_detector.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum GithubActionType {
    CREATE_TEAM("created"),
    DELETE_REPO("deleted"),
    OTHER("other");

    private static final Map<String, GithubActionType> STRING_TO_ENUM_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(GithubActionType::getActionName, action -> action));

    private final String actionName;

    GithubActionType(String actionName) {
        this.actionName = actionName;
    }

    public static GithubActionType getFromString(String actionName) {
        return STRING_TO_ENUM_MAP.getOrDefault(actionName, OTHER);
    }
}
