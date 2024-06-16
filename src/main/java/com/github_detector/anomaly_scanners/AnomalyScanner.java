package com.github_detector.anomaly_scanners;

import com.github_detector.common.GithubEventData;

public interface AnomalyScanner {
    public AnomalyScanResult scanEvent(GithubEventData eventData);
}
