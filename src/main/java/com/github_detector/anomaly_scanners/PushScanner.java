package com.github_detector.anomaly_scanners;

import com.github_detector.common.GithubEventData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class PushScanner implements AnomalyScanner{

    public static final String DETECTION_MESSAGE_FORMAT = "Suspicious activity detected, Push operation performed on repository: %s at a suspicious time: %s";

    private final LocalTime forbiddenPushStartTime;
    private final LocalTime forbiddenPushEndTime;

    public PushScanner(@Value("${scanners.push.forbidden-start-time}") String forbiddenPushStartTime,
                       @Value("${scanners.push.forbidden-end-time}")String forbiddenPushEndTime){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.forbiddenPushStartTime = LocalTime.parse(forbiddenPushStartTime, timeFormatter);
        this.forbiddenPushEndTime = LocalTime.parse(forbiddenPushEndTime, timeFormatter);
    }

    @Override
    public AnomalyScanResult scanEvent(GithubEventData eventData) {
        AnomalyScanResult result = new AnomalyScanResult();
        boolean anomalyDetected = eventData.getEventTimestamp().toLocalTime().isAfter(forbiddenPushStartTime)
                && eventData.getEventTimestamp().toLocalTime().isBefore(forbiddenPushEndTime);
        result.setAnomalyDetected(anomalyDetected);

        if(anomalyDetected){
            result.setMessage(String.format(DETECTION_MESSAGE_FORMAT
                    ,eventData.getRepositoryName()
                    ,eventData.getEventTimestamp()));
        }

        return result;
    }
}
