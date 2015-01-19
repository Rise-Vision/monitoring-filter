package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public interface MonitoringLogDataService {

    public MonitoringLogData getMonitoringLogData(String api, String clientId, String userid);
}
