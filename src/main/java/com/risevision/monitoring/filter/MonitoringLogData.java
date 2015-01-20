package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringLogData {

    private String api;
    private String clientId;
    private String userId;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
