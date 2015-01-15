package com.risevision.monitoring.filter;

import com.risevision.monitoring.filter.oauth.GoogleOAuthClientService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.ConfigurationException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class MonitoringFilterIntegrationTest {

    @Test
    public void testALogEntryIsCreated() throws IOException, ServletException, InterruptedException {

        // Call test API
        URL yahoo = new URL("http://localhost:8080/monitoring-filter/TestAPI");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));

        // Check log files has a log entry.
        boolean hasLogEntry = false;
        List<String> lines= Files.readAllLines(Paths.get("build/logs/buildLog.log"), Charset.forName("UTF-8"));
        for(String line:lines){
            if(line.contains("Monitoring:")){
                hasLogEntry = true;
            }
        }

        assertThat(hasLogEntry, is(true));
    }


}
