package com.github_detector.anomaly_scanners;

import com.github_detector.common.GithubEventData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateTeamScanner implements AnomalyScanner {

    public static final String DETECTION_MESSAGE_FORMAT = "Suspicious activity detected, team created with suspicious name: '%s'";
    private String[] invalidPrefixes;

    CreateTeamScanner(@Value("${scanners.create-team.invalid-prefixes}") String invalidPrefixes) {
        this.invalidPrefixes = invalidPrefixes.split(",");
    }

    @Override
    public AnomalyScanResult scanEvent(GithubEventData eventData) {
        AnomalyScanResult result = new AnomalyScanResult();

        String teamName = eventData.getTeamName();
        boolean anomalyDetected = false;

        for (String prefix : invalidPrefixes) {
            if (teamName.startsWith(prefix.trim())) {
                anomalyDetected = true;
                break;
            }
        }

        result.setAnomalyDetected(anomalyDetected);

        if (anomalyDetected) {
            result.setMessage(String.format(DETECTION_MESSAGE_FORMAT, teamName));
        }

        return result;
    }
}
