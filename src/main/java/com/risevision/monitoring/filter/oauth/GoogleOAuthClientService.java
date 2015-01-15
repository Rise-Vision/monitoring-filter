package com.risevision.monitoring.filter.oauth;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public interface GoogleOAuthClientService {

    public String lookupClientId(HttpServletRequest request);

}