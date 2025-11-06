package com.ds.ems.dtos;

public class LoginResponse {

    private String token;
    private String username;
    private boolean success;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, boolean success) {
        this.token = token;
        this.username = username;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

