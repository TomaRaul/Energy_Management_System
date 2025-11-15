package com.ds.ems.dtos;

public class RegisterResponse {

    private String username;
    private boolean success;

    public RegisterResponse() {
    }

    public RegisterResponse(String username, boolean success) {
        this.username = username;
        this.success = success;
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
                ", username='" + username + '\'' +
                '}';
    }
}
