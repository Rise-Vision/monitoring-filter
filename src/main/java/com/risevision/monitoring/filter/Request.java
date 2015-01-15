package com.risevision.monitoring.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class Request {


    private final String method;
    private final Cookie[] cookies;
    private final String authType;
    private final String contextPath;
    private final String pathInfo;
    private final String pathTranslated;
    private final String queryString;
    private final String remoteUser;
    private final String requestSessionId;
    private final String requestURI;
    private final StringBuffer requestURL;
    private final String servletPath;
    private final String scheme;
    private final String serverName;
    private final int localPort;
    private final String localName;
    private final String localAddr;
    private final String protocol;
    private final int remotePort;
    private final String remoteHost;
    private final String remoteAddr;
    private final boolean isSecure;
    private final int serverPort;

    public Request(HttpServletRequest httpServletRequest) {
        this.authType = httpServletRequest.getAuthType();
        this.contextPath = httpServletRequest.getContextPath();
        this.cookies = httpServletRequest.getCookies();
        this.method = httpServletRequest.getMethod();
        this.pathInfo = httpServletRequest.getPathInfo();
        this.pathTranslated = httpServletRequest.getPathTranslated();
        this.queryString = httpServletRequest.getQueryString();
        this.remoteUser = httpServletRequest.getRemoteUser();
        this.requestSessionId = httpServletRequest.getRequestedSessionId();
        this.requestURI = httpServletRequest.getRequestURI();
        this.requestURL = httpServletRequest.getRequestURL();
        this.servletPath = httpServletRequest.getServletPath();
        this.scheme = httpServletRequest.getScheme();
        this.serverName = httpServletRequest.getServerName();
        this.serverPort = httpServletRequest.getServerPort();
        this.isSecure = httpServletRequest.isSecure();
        this.remoteAddr = httpServletRequest.getRemoteAddr();
        this.remoteHost = httpServletRequest.getRemoteHost();
        this.remotePort = httpServletRequest.getRemotePort();
        this.protocol = httpServletRequest.getProtocol();
        this.localAddr = httpServletRequest.getLocalAddr();
        this.localName = httpServletRequest.getLocalName();
        this.localPort = httpServletRequest.getLocalPort();
    }

    public String getMethod() {
        return method;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public String getAuthType() {
        return authType;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getRequestSessionId() {
        return requestSessionId;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public int getServerPort() {
        return serverPort;
    }
}
