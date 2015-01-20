package com.risevision.monitoring.filter.oauth;

import javax.servlet.http.HttpServletRequest;

public interface GoogleOAuthClientService {

    public TokenInfo lookupTokenInfo(HttpServletRequest request);

}