package com.example.onbroading_login;

public class HelperClass {
    private String access_token;
    private String refresh_token;
    private int expires_in;
    private int refresh_expires_in;


    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}