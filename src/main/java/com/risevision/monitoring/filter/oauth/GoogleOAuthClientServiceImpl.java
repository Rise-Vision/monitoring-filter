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

    private final TokenInfoService tokenInfoService;

    public GoogleOAuthClientServiceImpl(TokenInfoService tokenInfoService) {
        this.tokenInfoService = tokenInfoService;
    }

    public TokenInfo lookupTokenInfo(HttpServletRequest request) {

        TokenInfo tokenInfo = null;

        if (request != null) {

            String auth = request.getHeader(AUTH_HEADER);

            if (auth != null && !auth.isEmpty()) {

                String token = auth.substring(TOKEN_PREFIX.length());

                if (!token.isEmpty()) {

                    tokenInfo = tokenInfoService.getTokenInfo(token);
                } else {
                    logger.info("There isn't Bearer token on the Authorization header");
                }
            } else {
                logger.info("There isn't Authorization on the request header");
            }
        } else {
            logger.warning("Request is null");
        }

        return tokenInfo;

    }
}
