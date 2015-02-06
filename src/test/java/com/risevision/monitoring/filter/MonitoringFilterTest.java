package com.risevision.monitoring.filter;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringFilterTest {

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private TimeService timeService;

    private MonitoringFilter monitoringFilter;
    private String apiParameterName;
    private String api;
    private String ip;
    private String host;
    private String bearerToken;
    private String authorization;
    private String authorizationValue;

    private String queueName;
    private String taskName;
    private String taskURL;
    private long time;
    private String URI;

    private LocalTaskQueueTestConfig localTaskQueueTestConfig;
    private LocalModulesServiceTestConfig localModulesServiceTestConfig;

    private LocalServiceTestHelper helper;


    @Before
    public void setUp() throws URISyntaxException, UnsupportedEncodingException {
        URL resource = MonitoringFilterTest.class.getResource("/queue.xml");

        String absolutePath = new File(resource.toURI()).getAbsolutePath();

        localTaskQueueTestConfig = new LocalTaskQueueTestConfig();
        localTaskQueueTestConfig.setQueueXmlPath(absolutePath);

        localModulesServiceTestConfig = new LocalModulesServiceTestConfig();
        localModulesServiceTestConfig.addAutomaticScalingModuleVersion("monitoring", "r1").addDefaultModuleVersion();

        helper = new LocalServiceTestHelper(localTaskQueueTestConfig, localModulesServiceTestConfig);

        helper.setUp();
        MockitoAnnotations.initMocks(this);
        monitoringFilter = new MonitoringFilter(filterConfig, timeService);

        this.apiParameterName = "apis";
        String apis = "CoreAPIv1,RiseAPIv0,TestAPIv0";
        this.api = "CoreAPIv1";


        ip = "1.1.1.1";
        host = "test.com";
        bearerToken = "xxxxxx";
        authorization = "Authorization";
        authorizationValue = "Bearer " + bearerToken;
        time = System.currentTimeMillis() / 1000L;

        URI = "/_ah/spi/com.risevision.core.api.v1." + api + ".getCompany";

        queueName = "monitoring";

        taskName = "monitoring_log";

        taskURL = "/queue?task=" + taskName + "&ip=" + ip + "&host=" + host + "&resource=" + URLEncoder.encode(URI, "UTF-8") + "&bearerToken=" + bearerToken + "&api=" + api + "&time=" + String.valueOf(time);

        given(timeService.getCurrentUnixTimestamp()).willReturn(time);
        given(filterConfig.getInitParameter(apiParameterName)).willReturn(apis);
        given(httpServletRequest.getRemoteAddr()).willReturn(ip);
        given(httpServletRequest.getRemoteHost()).willReturn(host);

    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testATaskIsAddedToTheQueue() throws IOException, ServletException {


        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(httpServletRequest.getHeader(authorization)).willReturn(authorizationValue);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(queueName);
        assertEquals(1, queueStateInfo.getTaskInfo().size());
        assertEquals(taskURL, queueStateInfo.getTaskInfo().get(0).getUrl());
    }

    @Test
    public void testATaskIsNotAddedToTheQueueBecauseThereIsNoAuthorizationValue() throws IOException, ServletException {


        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(httpServletRequest.getHeader(authorization)).willReturn("");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(queueName);
        assertEquals(0, queueStateInfo.getTaskInfo().size());
    }

    @Test
    public void testATaskIsNotAddedToTheQueueBecauseThereIsNoApiValue() throws IOException, ServletException {

        URI = "/view/display/xxxxxxxxxxx";

        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(httpServletRequest.getHeader(authorization)).willReturn(authorizationValue);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(queueName);
        assertEquals(0, queueStateInfo.getTaskInfo().size());
    }

    @Test
    public void testATaskIsNotAddedToTheQueueBecauseThereIsNoApiValueAndNoAuthorizationValue() throws IOException, ServletException {

        URI = "/view/display/xxxxxxxxxxx";

        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(httpServletRequest.getHeader(authorization)).willReturn("");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(queueName);
        assertEquals(0, queueStateInfo.getTaskInfo().size());
    }

    @Test
    public void testServiceAndApiNamesAreConfiguredAsTheFilterParameter() throws IOException, ServletException {

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(filterConfig).getInitParameter(apiParameterName);

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApisParameterIsNull() throws IOException, ServletException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn(null);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test(expected = ServletException.class)
    public void testThrowAnExceptionIfApisParameterIsEmpty() throws IOException, ServletException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn("");

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    @Test
    public void testWhenApiNamesParameterHasSingleElement() throws IOException, ServletException {

        given(filterConfig.getInitParameter(apiParameterName)).willReturn("CoreAPIv1");
        given(httpServletRequest.getRequestURI()).willReturn(URI);
        given(httpServletRequest.getHeader(authorization)).willReturn(authorizationValue);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(filterConfig).getInitParameter(apiParameterName);

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(queueName);
        assertEquals(1, queueStateInfo.getTaskInfo().size());
        assertEquals(taskURL, queueStateInfo.getTaskInfo().get(0).getUrl());

    }


}
