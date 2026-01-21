package com.ds.ems.dtos;

public class NotificationDTO {
    private String message;
    private String deviceId;

    public NotificationDTO() {
    }

    public NotificationDTO(String message, String deviceId) {
        this.message = message;
        this.deviceId = deviceId;
    }

    public String getMessage() {
        return message;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "message='" + message + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}