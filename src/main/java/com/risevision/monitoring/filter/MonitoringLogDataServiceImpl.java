package com.risevision.monitoring.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringLogDataServiceImpl implements MonitoringLogDataService {
    @Override
    public MonitoringLogData getMonitoringLogData(String api, String clientId, HttpServletRequest request) {

        MonitoringLogData monitoringLogData = new MonitoringLogData();
        monitoringLogData.setApi(api);
        monitoringLogData.setClientId(clientId);
        monitoringLogData.setRequest(new Request(request));

        return monitoringLogData;
    }
}
