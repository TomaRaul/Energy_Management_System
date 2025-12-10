package com.ds.ems.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_consumption")//,
        //uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "hour_timestamp"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "hour_timestamp", nullable = false)
    private LocalDateTime hourTimestamp;

    @Column(name = "hour_consumption", nullable = false)
    private Double hourConsumption;

//    @Column(name = "measurement_count")
//    private Integer measurementCount;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getHourTimestamp() {
        return hourTimestamp;
    }

    public void setHourTimestamp(LocalDateTime hourTimestamp) {
        this.hourTimestamp = hourTimestamp;
    }

    public Double getHourConsumption() {
        return hourConsumption;
    }

    public void setHourConsumption(Double totalKwh) {
        this.hourConsumption = totalKwh;
    }

//    public Integer getMeasurementCount() {
//        return measurementCount;
//    }
//
//    public void setMeasurementCount(Integer measurementCount) {
//        this.measurementCount = measurementCount;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
}
