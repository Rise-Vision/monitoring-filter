package com.risevision.monitoring.filter.oauth;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class GoogleOAuthClientServiceImpl implements GoogleOAuthClientService {

    private static final Logger logger = Logger.getLogger(GoogleOAuthClientService.class.getName());

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private TokenInfoService tokenInfoService;

    public GoogleOAuthClientServiceImpl(TokenInfoService tokenInfoService) {
        this.tokenInfoService = tokenInfoService;
    }

    public TokenInfo lookupTokenInfo(HttpServletRequest request) {

        TokenInfo tokenInfo = null;

        if (request != null) {

            String auth = request.getHeader(AUTH_HEADER);

            if (auth != null && !auth.isEmpty()) {

                String token = auth.substring(TOKEN_PREFIX.length());

                if (token != null && !token.isEmpty()) {

                    tokenInfo = tokenInfoService.getTokenInfo(token);
                }
            }
        }

        return tokenInfo;

    }
}
