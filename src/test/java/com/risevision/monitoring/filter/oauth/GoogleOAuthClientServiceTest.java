package com.risevision.monitoring.filter.oauth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class GoogleOAuthClientServiceTest {

    private GoogleOAuthClientService googleOAuthClientService;

    @Mock
    private TokenInfoService tokenInfoService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private TokenInfo tokenInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        googleOAuthClientService = new GoogleOAuthClientServiceImpl(tokenInfoService);
    }

    @Test
    public void testLookUpTokenInfo() {
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(tokenInfo);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);

        assertThat(actualTokenInfo, is(tokenInfo));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfTokenInfoCannotBeRetrieved() {
        String authorization = "Bearer xxxxxxxxxx";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);
        given(tokenInfoService.getTokenInfo(token)).willReturn(null);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfRequestIsNull() {

        String token = "xxxxxxxxxx";

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(null);

        verify(httpServletRequest, never()).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfRequestHeaderDoesNotContainAuthorization() {

        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(null);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfTokenIsEmpty() {

        String authorization = "Bearer ";
        String token = "xxxxxxxxxx";

        given(httpServletRequest.getHeader("Authorization")).willReturn(authorization);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(httpServletRequest);

        verify(httpServletRequest).getHeader("Authorization");
        verify(tokenInfoService, never()).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }
}
