package com.ds.ems.dtos;

public class ConsumptionDTO {
    private Integer hour;
    private Double hourConsumption;

    public ConsumptionDTO(Integer hour, Double hourConsumption) {
        this.hour = hour;
    this.hourConsumption = hourConsumption;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Double getHourConsumption() {
        return hourConsumption;
    }

    public void setHourConsumption(Double hourConsumption) {
        this.hourConsumption = hourConsumption;
    }
}
