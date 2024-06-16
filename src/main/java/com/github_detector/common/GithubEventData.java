package com.github_detector.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GithubEventData {

    private GithubEventType githubEventType;
    private GithubActionType action;
    private String teamName;
    private String repositoryName;
    private LocalDateTime repositoryCreatedAt;
    private LocalDateTime eventTimestamp;
}
