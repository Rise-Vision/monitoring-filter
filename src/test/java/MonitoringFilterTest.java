import com.risevision.monitoring.filter.JsonService;
import com.risevision.monitoring.filter.MonitoringFilter;
import com.risevision.monitoring.filter.MonitoringLogData;
import com.risevision.monitoring.filter.MonitoringLogDataService;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
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
    public void setup(){
        MockitoAnnotations.initMocks(this);
        monitoringFilter = new MonitoringFilter(filterConfig,googleOAuthClientService,monitoringLogDataService,jsonService,logger);
    }

    @Test
    public void testALogEntryIsCreated() throws IOException, ServletException {
        String apiName = "apiName";
        String clientId = "xxxxxxx";

        given(monitoringLogDataService.getMonitoringLogData(apiName,clientId,httpServletRequest)).willReturn(monitoringLogData);
        given(jsonService.getJson(monitoringLogData)).willReturn(null);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: clientId={0}, data={1}"), eq(new Object[]{null,null}));
    }

    @Test
    public void testApiNameAndClientIdIsOntheLogEntry() throws IOException, ServletException {

        String apiName = "apiName";
        String clientId = "xxxxxxx";
        String data = "{api: \"Core\", clientId: \"xxxxxxx\"}";

        given(monitoringLogData.getApi()).willReturn("Core");
        given(monitoringLogData.getClientId()).willReturn(clientId);
        given(googleOAuthClientService.lookupClientId(httpServletRequest)).willReturn(clientId);
        given(monitoringLogDataService.getMonitoringLogData("Core", clientId, httpServletRequest)).willReturn(monitoringLogData);
        given(jsonService.getJson(monitoringLogData)).willReturn(data);

        monitoringFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(logger).log(eq(Level.INFO), eq("Monitoring: clientId={0}, data={1}"), eq(new Object[]{clientId, data}));
    }

}
