package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringLogDataServiceImpl implements MonitoringLogDataService {
    @Override
    public MonitoringLogData getMonitoringLogData(String service, String api, String clientId) {

        MonitoringLogData monitoringLogData = new MonitoringLogData();
        monitoringLogData.setService(service);
        monitoringLogData.setApi(api);
        monitoringLogData.setClientId(clientId);
        return monitoringLogData;
    }
}
