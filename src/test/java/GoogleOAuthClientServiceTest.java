import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.filter.oauth.TokenInfo;
import com.risevision.monitoring.filter.oauth.TokenInfoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class GoogleOAuthClientServiceTest {

    private GoogleOAuthClientService googleOAuthClientService;

    @Mock
    private TokenInfoService tokenInfoService;

    @Mock
    private TokenInfo tokenInfo;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        googleOAuthClientService = new GoogleOAuthClientService(tokenInfoService);
    }

    @Test
    public void testLookUpClientIdWithAValidToken(){
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";
        String expectedClientId = "ccccccccc";

        when(httpServletRequest.getHeader("Authorization")).thenReturn(authorization);
        when(tokenInfoService.getTokenInfo(token)).thenReturn(tokenInfo);
        when(tokenInfo.getIssued_to()).thenReturn(expectedClientId);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, atLeastOnce()).getIssued_to();

        assertThat(expectedClientId, is(equalTo(actualClientId)));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfTokenInfoCannotBeRetrieved(){
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        when(httpServletRequest.getHeader("Authorization")).thenReturn(authorization);
        when(tokenInfoService.getTokenInfo(token)).thenReturn(null);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, never()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }
}
