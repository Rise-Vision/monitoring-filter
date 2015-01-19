package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringLogDataServiceImpl implements MonitoringLogDataService {
    @Override
    public MonitoringLogData getMonitoringLogData(String service, String api, String clientId, String userId) {

        MonitoringLogData monitoringLogData = new MonitoringLogData();
        monitoringLogData.setApi(api);
        monitoringLogData.setClientId(clientId);
        monitoringLogData.setUserId(userId);
        return monitoringLogData;
    }
}
