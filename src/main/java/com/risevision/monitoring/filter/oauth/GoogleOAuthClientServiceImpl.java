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

    public String lookupClientId(HttpServletRequest request) {

        String clientId = null;

        if (request != null) {

            String auth = request.getHeader(AUTH_HEADER);

            if (auth != null && !auth.isEmpty()) {

                String token = auth.substring(TOKEN_PREFIX.length());

                if (token != null && !token.isEmpty()) {

                    TokenInfo tokenInfo = tokenInfoService.getTokenInfo(token);

                    if (tokenInfo != null) {

                        if (tokenInfo.getIssued_to() != null && !tokenInfo.getIssued_to().isEmpty()) {

                            clientId = tokenInfo.getIssued_to();

                        } else {

                            logger.info("TokenInfo does not contain a client id(issued_to)");

                        }
                    } else {
                        logger.info("TokenInfo is null. Something happened that it could not be retrieved from OAuth token info service");
                    }
                }
            }
        }

        logger.info("Client ID: " + clientId);

        return clientId;

    }
}
