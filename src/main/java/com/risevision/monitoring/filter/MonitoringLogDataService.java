package com.risevision.monitoring.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public interface MonitoringLogDataService {

    public MonitoringLogData getMonitoringLogData(String api, String clientId, HttpServletRequest request);
}
