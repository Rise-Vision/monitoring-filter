package com.risevision.monitoring.filter;

import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.filter.oauth.TokenInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringFilterTest {

    private String apiParameterName;
    private String api;
    private String clientId;
    private String userId;


    @Mock
    private GoogleOAuthClientService googleOAuthClientService;
    @Mock
    private MonitoringLogDataService monitoringLogDataService;
    @Spy
    private MonitoringLogDataServiceImpl monitoringLogDataServiceSpy;
    @Mock
    private MonitoringLogData monitoringLogData;
    @Spy
    private MonitoringLogData monitoringLogDataSpy;
    @Mock
    private JsonService jsonService;
    @Spy
    private JsonServiceImpl jsonServiceSpy;
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
    @Mock
    private TokenInfo tokenInfo;

    private MonitoringFilter monitoringFilter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        monitoringFilter = new MonitoringFilter(filterConfig, googleOAuthClientService, monitoringLogDataService, jsonService, logger);

        this.apiParameterName = "apis";
        String apis = "CoreAPIv1,RiseAPIv0,TestAPIv0";
        this.api = "CoreAPIv1";
        String URI = "/_ah/spi/com.risevision.core.api.v1." + api + ".getCompany";
        this.clientId = "xxxxxxx";
        this.userId = "example@gmail.com";

        given(filterConfig.getInitParameter(apiParameterName)).willReturn(apis);
        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(tokenInfo.getIssued_to()).willReturn(clientId);
        given(tokenInfo.getEmail()).willReturn(userId);
        given(googleOAuthClientService.lookupTokenInfo(httpServletRequest)).willReturn(tokenInfo);
        given(monitoringLogDataService.getMonitoringLogData(api, clientId, userId)).willReturn(monitoringLogData);
    }

    @Test
    public void testALogEntryIsCreated() throws IOException, ServletException {

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: data={0}"), anyString());
    }

    @Test
    public void testDataIsOnTheLogEntry() throws IOException, ServletException {
        String data = "{\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\",\"userId\":\"" + userId + "\"}";

        monitoringFilter = new MonitoringFilter(filterConfig, googleOAuthClientService, monitoringLogDataServiceSpy, jsonServiceSpy, logger);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(monitoringLogDataServiceSpy).getMonitoringLogData(api, clientId, userId);
        verify(jsonServiceSpy).getJson(anyObject(), eq(MonitoringLogData.class));

        verify(logger).log(eq(Level.INFO), eq("Monitoring: data={0}"), eq(data));
    }

    @Test
    public void testDataWillNotContainClientIdAndUserIdOnTheLogEntryWhenTokenInfoIsNull() throws IOException, ServletException {
        String data = "{\"api\":\"" + api + "\"}";

        given(googleOAuthClientService.lookupTokenInfo(httpServletRequest)).willReturn(null);
        monitoringFilter = new MonitoringFilter(filterConfig, googleOAuthClientService, monitoringLogDataServiceSpy, jsonServiceSpy, logger);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(monitoringLogDataServiceSpy).getMonitoringLogData(api, null, null);
        verify(jsonServiceSpy).getJson(anyObject(), eq(MonitoringLogData.class));

        verify(logger).log(eq(Level.INFO), eq("Monitoring: data={0}"), eq(data));
    }

    @Test
    public void testServiceAndApiNamesAreConfiguredAsTheFilterParameter() throws IOException, ServletException {

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(filterConfig).getInitParameter(apiParameterName);

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApisParameterIsNull() throws IOException, ServletException, ConfigurationException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn(null);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApisParameterIsEmpty() throws IOException, ServletException, ConfigurationException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn("");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test
    public void testWhenApiNamesParameterHasSingleElement() throws IOException, ServletException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn("CoreAPIv1");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(filterConfig).getInitParameter(apiParameterName);
        verify(logger).log(eq(Level.INFO), eq("Monitoring: data={0}"), anyString());
    }

    @Test
    public void testAttributeForTheClientIdISAddedToTheRequest() throws IOException, ServletException {

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupTokenInfo(httpServletRequest);
        verify(httpServletRequest).setAttribute("clientId", clientId);

    }

    @Test
    public void testAttributeForTheClientIdISNOTAddedToTheRequestWhenItIsNull() throws IOException, ServletException {
        given(tokenInfo.getIssued_to()).willReturn(null);
        given(googleOAuthClientService.lookupTokenInfo(httpServletRequest)).willReturn(tokenInfo);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupTokenInfo(httpServletRequest);
        verify(httpServletRequest, never()).setAttribute(eq("clientId"), anyString());

    }

    @Test
    public void testAttributeForTheClientIdISNOTAddedToTheRequestWhenItIsEmpty() throws IOException, ServletException {
        given(tokenInfo.getIssued_to()).willReturn("");
        given(googleOAuthClientService.lookupTokenInfo(httpServletRequest)).willReturn(tokenInfo);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(googleOAuthClientService).lookupTokenInfo(httpServletRequest);
        verify(httpServletRequest, never()).setAttribute(eq("clientId"), anyString());

    }

}
