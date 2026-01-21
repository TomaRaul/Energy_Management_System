package com.ds.ems.dtos;

import java.util.Objects;

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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String userId) {
        this.deviceId = userId;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "message='" + message + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, deviceId);
    }
}