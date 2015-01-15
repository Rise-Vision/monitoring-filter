package com.risevision.monitoring.filter;

import com.google.gson.Gson;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientServiceImpl;
import com.risevision.monitoring.filter.oauth.TokenInfoServiceImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/7/15.
 */
public class MonitoringFilter implements Filter {

    private FilterConfig filterConfig;
    private final Logger logger;
    private GoogleOAuthClientService googleOAuthClientService;
    private MonitoringLogDataService monitoringLogDataService;
    private JsonService jsonService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.googleOAuthClientService = new GoogleOAuthClientServiceImpl(new TokenInfoServiceImpl());
        this.monitoringLogDataService = new MonitoringLogDataServiceImpl();
        this.jsonService = new JsonServiceImpl();
    }

    public MonitoringFilter(){
        logger = Logger.getLogger( MonitoringFilter.class.getName());
    }

    /**
     * This constructor is just fot testing purpose.
     * The filter will actually be instantiate by the servlet contest and the init method will be called
     * @param filterConfig
     * @param googleOAuthClientService
     */
    public MonitoringFilter(FilterConfig filterConfig, GoogleOAuthClientService googleOAuthClientService, MonitoringLogDataService monitoringLogDataService, JsonService jsonService,Logger logger){
        this.filterConfig = filterConfig;
        this.googleOAuthClientService = googleOAuthClientService;
        this.monitoringLogDataService = monitoringLogDataService;
        this.jsonService = jsonService;
        this.logger = logger;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String clientId = googleOAuthClientService.lookupClientId(request);

        MonitoringLogData monitoringLogData = monitoringLogDataService.getMonitoringLogData("Core",clientId,request);

        logger.log(Level.INFO, "Monitoring: clientId={0}, data={1}", new Object[]{ clientId, jsonService.getJson(monitoringLogData) });

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
