package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringLogData {

    private String service;
    private String api;
    private String clientId;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

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
}
