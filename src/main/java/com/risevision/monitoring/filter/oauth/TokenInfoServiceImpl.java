package com.risevision.monitoring.filter.oauth;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class TokenInfoServiceImpl implements TokenInfoService {

    private static final Logger logger = Logger.getLogger(TokenInfoServiceImpl.class.getName());

    private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";

    @Override
    public TokenInfo getTokenInfo(String token) {
        String jsonText = getJsonFromUrl(GOOGLE_TOKEN_INFO_URL + token);

        if (jsonText == null || jsonText.isEmpty())
            return null;

        return new Gson().fromJson(jsonText, TokenInfo.class);
    }

    private String getJsonFromUrl(String jsonUrl) {

        String result;

        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL(jsonUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            try {

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                reader.close();
            }

            result = sb.toString();

        } catch (Exception e) {
            logger.warning(String.format("Error retrieving JSON from %s : %s", jsonUrl, e.toString()));
            result = null;
        }

        return result;
    }
}
