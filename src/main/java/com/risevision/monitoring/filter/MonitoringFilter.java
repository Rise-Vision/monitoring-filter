package com.risevision.monitoring.filter;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * Created by rodrigopavezi on 1/7/15.
 */
public class MonitoringFilter implements Filter {

    private static final String APIS_PARAMETER = "apis";
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String APP_ENGINE_MODULE = "monitoring";

    private static final String MONITORING_QUEUE_NAME = "apiMonitoring";
    private static final String MONITORING_TASK_NAME = "monitoring_log";

    private static final String TASK_PARAMETER = "task";
    private static final String IP_PARAMETER = "ip";
    private static final String HOST_PARAMETER = "host";
    private static final String RESOURE_PARAMETER = "resource";
    private static final String BEARER_TOKEN_PARAMETER = "bearerToken";
    private static final String API_PARAMETER = "api";
    private static final String TIME_PARAMETER = "time";


    private final Logger logger = Logger.getLogger(MonitoringFilter.class.getName());
    ;
    private FilterConfig filterConfig;
    private TimeService timeService;


    /**
     * This constructor is just fot testing purpose.
     * The filter will actually be instantiate by the servlet contest and the init method will be called
     *
     * @param filterConfig
     */
    public MonitoringFilter(FilterConfig filterConfig, TimeService timeService) {
        this.filterConfig = filterConfig;
        this.timeService = timeService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.timeService = new TimeServiceImpl();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

        String apis = filterConfig.getInitParameter(APIS_PARAMETER);

        if (apis == null || apis.isEmpty()) {
            throw new ServletException("Filter parameter \"" + APIS_PARAMETER + "\" must be set. It cannot be null or empty.");
        }

        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            String api = getAPIFromRequest(apis, request.getRequestURI());
            String bearerToken = getBearerTokenFromRequest(request);


            if (bearerToken != null && !bearerToken.isEmpty() && api != null && !api.isEmpty()) {

                ModulesService modulesApi = ModulesServiceFactory.getModulesService();

                QueueFactory.getQueue(MONITORING_QUEUE_NAME).add(withUrl("/queue")
                        .param(TASK_PARAMETER, MONITORING_TASK_NAME)
                        .param(IP_PARAMETER, request.getRemoteAddr())
                        .param(HOST_PARAMETER, request.getRemoteHost())
                        .param(RESOURE_PARAMETER, request.getRequestURI())
                        .param(BEARER_TOKEN_PARAMETER, bearerToken)
                        .param(API_PARAMETER, api)
                        .param(TIME_PARAMETER, String.valueOf(timeService.getCurrentUnixTimestamp()))
                        .method(TaskOptions.Method.GET)
                        .header("Host", modulesApi.getVersionHostname(APP_ENGINE_MODULE, modulesApi.getDefaultVersion(APP_ENGINE_MODULE))));
            }
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Monitoring log task could not be added to the monitoring queue", exception);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getBearerTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String auth = request.getHeader(AUTH_HEADER);

        if (auth != null && !auth.isEmpty()) {

            token = auth.substring(TOKEN_PREFIX.length());
        }

        return token;
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
