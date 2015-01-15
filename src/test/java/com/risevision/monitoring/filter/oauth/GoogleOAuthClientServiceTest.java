package com.risevision.monitoring.filter.oauth;

import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.filter.oauth.GoogleOAuthClientServiceImpl;
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
import static org.mockito.BDDMockito.given;
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
    public void setup() {
        MockitoAnnotations.initMocks(this);
        googleOAuthClientService = new GoogleOAuthClientServiceImpl(tokenInfoService);
    }

    @Test
    public void testLookUpClientIdWithAValidToken() {
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";
        String expectedClientId = "ccccccccc";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(tokenInfo);
        given(tokenInfo.getIssued_to()).willReturn(expectedClientId);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, atLeastOnce()).getIssued_to();

        assertThat(expectedClientId, is(equalTo(actualClientId)));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfTokenInfoCannotBeRetrieved() {
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(null);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, never()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfRequestIsNull() {

        String token = "xxxxxxxxxx";

        String actualClientId = googleOAuthClientService.lookupClientId(null);

        verify(httpServletRequest, never()).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);
        verify(tokenInfo, never()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfRequestHeaderDoesNotContainAuthorization() {

        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(null);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);
        verify(tokenInfo, never()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfTokenIsEmpty() {

        String authorization = "Bearer ";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);
        verify(tokenInfo, never()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfTokenInfoHasNullClientId() {

        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(tokenInfo);
        given(tokenInfo.getIssued_to()).willReturn(null);

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, atLeastOnce()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }

    @Test
    public void testLookUpClientIdReturnsNullIfTokenInfoHasEmptyClientId() {

        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(tokenInfo);
        given(tokenInfo.getIssued_to()).willReturn("");

        String actualClientId = googleOAuthClientService.lookupClientId(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);
        verify(tokenInfo, atLeastOnce()).getIssued_to();

        assertThat(actualClientId, is(nullValue()));
    }
}
