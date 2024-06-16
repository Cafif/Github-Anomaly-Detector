package com.github_detector.services;

import com.github_detector.anomaly_scanners.*;
import com.github_detector.common.GithubActionType;
import com.github_detector.common.GithubEventData;
import com.github_detector.common.GithubEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubEventService {

    @Autowired
    DeleteRepoScanner deleteRepoScanner;
    @Autowired
    PushScanner pushScanner;
    @Autowired
    CreateTeamScanner createTeamScanner;
    @Autowired
    NotificationService notificationService;



    public void processGithubEvent(GithubEventData eventData){
        List<AnomalyScanner> scanners = getRelevantAnomalyScanners(eventData.getGithubEventType(), eventData.getGithubActionType());

        Set<String> anomalyMessages = scanners.stream()
                .map(scanner -> scanner.scanEvent(eventData))
                .filter(AnomalyScanResult::isAnomalyDetected)
                .map(AnomalyScanResult::getMessage)
                .collect(Collectors.toSet());

        notificationService.notify(anomalyMessages);
    }

    private List<AnomalyScanner> getRelevantAnomalyScanners(GithubEventType eventType, GithubActionType actionType){
        List<AnomalyScanner> relevantScanners = new ArrayList<>();
        switch(eventType){
            case REPOSITORY:
                if(Objects.equals(GithubActionType.DELETE_REPO,actionType)){
                    relevantScanners.add(deleteRepoScanner);
                }
            case PUSH:
                relevantScanners.add(pushScanner);
            case TEAM:
                if(Objects.equals(GithubActionType.CREATE_TEAM,actionType)){
                    relevantScanners.add(createTeamScanner);
            }
        }

        return relevantScanners;
    }
}
