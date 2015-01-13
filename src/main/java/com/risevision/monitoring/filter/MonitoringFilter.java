package com.risevision.monitoring.filter;

import com.google.gson.Gson;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
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
    private static final Logger logger = Logger.getLogger( MonitoringFilter.class.getName() );
    private GoogleOAuthClientService googleOAuthClientService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        googleOAuthClientService = new GoogleOAuthClientService(new TokenInfoServiceImpl());

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String clientId = googleOAuthClientService.lookupClientId(request);

        MonitoringLogMessage monitoringLogMessage = new MonitoringLogMessage("CORE", request.getRequestURI(),request.getRemoteAddr(),request.getRemoteHost(), clientId);

        logger.log(Level.INFO, "Monitoring: clientId={0}, data={1}", new Object[]{ clientId, new Gson().toJson(monitoringLogMessage) });

        filterChain.doFilter(servletRequest,servletResponse);
    }

    class MonitoringLogMessage {

        String api;
        String resource;
        String ipAddress;
        String hostname;
        String clientId;

        MonitoringLogMessage(String api, String resource, String ipAddress, String hostname, String clientId){
            this.api = api;
            this.resource = resource;
            this.ipAddress = ipAddress;
            this.hostname = hostname;
            this.clientId = clientId;
        }
    }


    @Override
    public void destroy() {

    }
}
