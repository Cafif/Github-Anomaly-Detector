package com.github_detector.anomaly_scanners;

import com.github_detector.common.GithubEventData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeleteRepoScanner implements AnomalyScanner{

    public static final String DETECTION_MESSAGE_FORMAT = "Suspicious activity detected, repository '%s' deleted within %s minutes of creation.\n Created: %s\n Deleted: %s";
    private int minimumMinutes;

    DeleteRepoScanner(@Value("${scanners.delete-repo.min-minutes}") int minimumMinutes ){
        this.minimumMinutes = minimumMinutes;
    }

    @Override
    public AnomalyScanResult scanEvent(GithubEventData eventData) {
        AnomalyScanResult result = new AnomalyScanResult();
        boolean anomalyDetected = eventData.getEventTimestamp().minusMinutes(minimumMinutes).isBefore(eventData.getRepositoryCreatedAt());
        result.setAnomalyDetected(anomalyDetected);

        if(anomalyDetected){
            result.setMessage(String.format(DETECTION_MESSAGE_FORMAT
                    ,eventData.getRepositoryName()
                    ,minimumMinutes
                    ,eventData.getRepositoryCreatedAt()
                    ,eventData.getEventTimestamp()));
        }

        return result;
    }
}
