package com.risevision.monitoring.filter.oauth;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class TokenInfo {

    private String issued_to;
    private String audience;
    private String user_id;
    private String scope;
    private Integer expires_in;
    private String email;
    private Boolean verified_email;
    private String access_type;

    public String getIssued_to() {
        return issued_to;
    }

    public String getAudience() {
        return audience;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getScope() {
        return scope;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getVerified_email() {
        return verified_email;
    }

    public String getAccess_type() {
        return access_type;
    }
}
