package com.risevision.monitoring.filter;

/**
 * Created by rodrigopavezi on 2/6/15.
 */
public class TimeServiceImpl implements TimeService {

    @Override
    public long getCurrentUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
