package com.risevision.monitoring.filter.oauth;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class GoogleOAuthClientService {

    private static final Logger logger = Logger.getLogger( GoogleOAuthClientService.class.getName() );
	
	private static final String AUTH_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

    private TokenInfoService tokenInfoService;

    public GoogleOAuthClientService(TokenInfoService tokenInfoService){
        this.tokenInfoService = tokenInfoService;
    }
	
	public String lookupClientId(HttpServletRequest request) {
		
		if (request == null) 
			return null;
		
		String auth = request.getHeader(AUTH_HEADER);
		
		if (auth == null || auth.isEmpty())
			return null;
		
		String token = auth.substring(TOKEN_PREFIX.length());
		
		if (token.isEmpty())
			return null;

		TokenInfo tokenInfo = tokenInfoService.getTokenInfo(token);

			
		return tokenInfo == null || tokenInfo.getIssued_to() == null || tokenInfo.getIssued_to().isEmpty() ? null : tokenInfo.getIssued_to();
		
	}
	


}