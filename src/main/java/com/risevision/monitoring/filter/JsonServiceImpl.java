package com.risevision.monitoring.filter;

import com.google.gson.Gson;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class JsonServiceImpl implements JsonService {
    @Override
    public String getJson(Object object) {

       return new Gson().toJson(object, object.getClass());
    }
}
