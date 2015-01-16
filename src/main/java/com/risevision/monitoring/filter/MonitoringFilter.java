package com.risevision.monitoring.filter;

import com.google.gson.Gson;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientServiceImpl;
import com.risevision.monitoring.filter.oauth.TokenInfoServiceImpl;

import javax.naming.ConfigurationException;
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

    private static final String API_NAME_PARAMETER = "api";
    private static final String CLIENT_ID_REQUEST_ATTRIBUTE_NAME = "clientId";

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

        String api = filterConfig.getInitParameter(API_NAME_PARAMETER);

        if (api == null || api.isEmpty()) {
            throw new ServletException("Filter parameter \"" + API_NAME_PARAMETER + "\" must be set. It cannot be null or empty.");
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String clientId = googleOAuthClientService.lookupClientId(request);

        MonitoringLogData monitoringLogData = monitoringLogDataService.getMonitoringLogData(api,clientId,request);

        logger.log(Level.INFO, "Monitoring: clientId={0}, data={1}", new Object[]{ clientId, jsonService.getJson(monitoringLogData) });

        if (clientId != null && !clientId.isEmpty()) {
            request.setAttribute(CLIENT_ID_REQUEST_ATTRIBUTE_NAME, clientId);
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
