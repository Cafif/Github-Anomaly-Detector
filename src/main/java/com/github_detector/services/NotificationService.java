package com.github_detector.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    @Value("${notify.log}")
    private boolean logEnabled;

    @Value("${notify.console}")
    private boolean consoleEnabled;

    public void notify(Set<String> anomalyMessages) {
        if (logEnabled) {
            logAnomalies(anomalyMessages);
        }

        if (consoleEnabled) {
            consoleAnomalies(anomalyMessages);
        }
    }

    private void logAnomalies(Set<String> anomalyMessages) {
        anomalyMessages.forEach(logger::info);
    }

    private void consoleAnomalies(Set<String> anomalyMessages) {
        if(!logEnabled) {
            anomalyMessages.forEach(System.out::println);
        }
    }
}
