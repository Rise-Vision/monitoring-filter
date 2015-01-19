package com.risevision.monitoring.filter;

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

    private static final String APIS_PARAMETER = "apis";
    private static final String SERVICE_PARAMETER = "service";
    private static final String CLIENT_ID_REQUEST_ATTRIBUTE_NAME = "clientId";
    private final Logger logger;
    private FilterConfig filterConfig;
    private GoogleOAuthClientService googleOAuthClientService;
    private MonitoringLogDataService monitoringLogDataService;
    private JsonService jsonService;

    public MonitoringFilter() {
        logger = Logger.getLogger(MonitoringFilter.class.getName());
    }

    /**
     * This constructor is just fot testing purpose.
     * The filter will actually be instantiate by the servlet contest and the init method will be called
     *
     * @param filterConfig
     * @param googleOAuthClientService
     */
    public MonitoringFilter(FilterConfig filterConfig, GoogleOAuthClientService googleOAuthClientService, MonitoringLogDataService monitoringLogDataService, JsonService jsonService, Logger logger) {
        this.filterConfig = filterConfig;
        this.googleOAuthClientService = googleOAuthClientService;
        this.monitoringLogDataService = monitoringLogDataService;
        this.jsonService = jsonService;
        this.logger = logger;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.googleOAuthClientService = new GoogleOAuthClientServiceImpl(new TokenInfoServiceImpl());
        this.monitoringLogDataService = new MonitoringLogDataServiceImpl();
        this.jsonService = new JsonServiceImpl();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String service = filterConfig.getInitParameter(SERVICE_PARAMETER);
        String apis = filterConfig.getInitParameter(APIS_PARAMETER);

        if (service == null || service.isEmpty()) {
            throw new ServletException("Filter parameter \"" + SERVICE_PARAMETER + "\" must be set. It cannot be null or empty.");
        }

        if (apis == null || apis.isEmpty()) {
            throw new ServletException("Filter parameter \"" + APIS_PARAMETER + "\" must be set. It cannot be null or empty.");
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String api = getAPIFromRequest(apis, request.getRequestURI());

        String clientId = googleOAuthClientService.lookupClientId(request);

        MonitoringLogData monitoringLogData = monitoringLogDataService.getMonitoringLogData(service, api, clientId);

        logger.log(Level.INFO, "Monitoring: data={1}", jsonService.getJson(monitoringLogData));

        if (clientId != null && !clientId.isEmpty()) {
            request.setAttribute(CLIENT_ID_REQUEST_ATTRIBUTE_NAME, clientId);
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    private String getAPIFromRequest(String apis, String URI) {

        String SEPARATOR = ",";
        String apiRequested = null;
        if (apis.contains(SEPARATOR)) {
            String[] apiList = apis.split(",");
            for (String api : apiList) {
                if (URI.contains(api)) {
                    apiRequested = api;
                }
            }
        } else {
            if (URI.contains(apis)) {
                apiRequested = apis;
            }
        }

        return apiRequested;
    }

    @Override
    public void destroy() {

    }
}
