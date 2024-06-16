package com.github_detector.anomaly_scanners;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnomalyScanResult {
    private boolean anomalyDetected;
    private String message;
}
