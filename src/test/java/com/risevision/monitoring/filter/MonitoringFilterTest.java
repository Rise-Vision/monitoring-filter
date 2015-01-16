package com.risevision.monitoring.filter;

import com.risevision.monitoring.filter.JsonService;
import com.risevision.monitoring.filter.MonitoringFilter;
import com.risevision.monitoring.filter.MonitoringLogData;
import com.risevision.monitoring.filter.MonitoringLogDataService;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.ConfigurationException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringFilterTest {

    @Mock
    private GoogleOAuthClientService googleOAuthClientService;
    @Mock
    private MonitoringLogDataService monitoringLogDataService;
    @Mock
    private MonitoringLogData monitoringLogData;
    @Mock
    private JsonService jsonService;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private Logger logger;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;

    private MonitoringFilter monitoringFilter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        monitoringFilter = new MonitoringFilter(filterConfig, googleOAuthClientService, monitoringLogDataService, jsonService, logger);
    }

    @Test
    public void testALogEntryIsCreated() throws IOException, ServletException {
        String parameterName = "api";
        String api = "Core";
        String clientId = "xxxxxxx";

        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);
        given(monitoringLogDataService.getMonitoringLogData(api, clientId, httpServletRequest)).willReturn(monitoringLogData);
        given(jsonService.getJson(monitoringLogData)).willReturn(null);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(monitoringLogDataService).getMonitoringLogData(api, clientId, httpServletRequest);
        verify(jsonService).getJson(monitoringLogData);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: clientId={0}, data={1}"), eq(new Object[]{clientId, null}));
    }

    @Test
    public void testApiNameAndClientIdIsOnTheLogEntry() throws IOException, ServletException {
        String parameterName = "api";
        String api = "Core";
        String clientId = "xxxxxxx";
        String data = "{api: \"" + api + "\", clientId: \"xxxxxxx\"}";

        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);
        given(monitoringLogDataService.getMonitoringLogData(api, clientId, httpServletRequest)).willReturn(monitoringLogData);
        given(jsonService.getJson(monitoringLogData)).willReturn(data);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(monitoringLogDataService).getMonitoringLogData(api, clientId, httpServletRequest);
        verify(jsonService).getJson(monitoringLogData);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: clientId={0}, data={1}"), eq(new Object[]{clientId, data}));
    }

    @Test
    public void testApiNameIsConfiguredAsTheFilterParameter() throws IOException, ServletException {
        String parameterName = "api";
        String api = "APIname";
        String clientId = "xxxxxxx";
        String data = "{api: \"" + api + "\", clientId: \"xxxxxxx\"}";


        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);
        given(monitoringLogDataService.getMonitoringLogData(api, clientId, httpServletRequest)).willReturn(monitoringLogData);
        given(jsonService.getJson(monitoringLogData)).willReturn(data);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(filterConfig).getInitParameter(parameterName);
        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(monitoringLogDataService).getMonitoringLogData(api, clientId, httpServletRequest);
        verify(jsonService).getJson(monitoringLogData);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: clientId={0}, data={1}"), eq(new Object[]{clientId, data}));

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApiIsNull() throws IOException, ServletException, ConfigurationException {
        String parameterName = "api";

        given(filterConfig.getInitParameter(parameterName)).willReturn(null);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApiIsEmpty() throws IOException, ServletException, ConfigurationException {
        String parameterName = "api";

        given(filterConfig.getInitParameter(parameterName)).willReturn("");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test
    public void testAttributeForTheClientIdISAddedToTheRequest() throws IOException, ServletException {
        String parameterName = "api";
        String api = "Core";
        String clientId = "xxxxxxx";

        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(httpServletRequest).setAttribute("clientId", clientId);

    }

    @Test
    public void testAttributeForTheClientIdISNOTAddedToTheRequestWhenItIsNull() throws IOException, ServletException {
        String parameterName = "api";
        String api = "Core";
        String clientId = null;

        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(httpServletRequest, never()).setAttribute(eq("clientId"), anyString());

    }

    @Test
    public void testAttributeForTheClientIdISNOTAddedToTheRequestWhenItIsEmpty() throws IOException, ServletException {
        String parameterName = "api";
        String api = "Core";
        String clientId = "";

        given(filterConfig.getInitParameter(parameterName)).willReturn(api);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupClientId(httpServletRequest);
        verify(httpServletRequest, never()).setAttribute(eq("clientId"), anyString());

    }
}
