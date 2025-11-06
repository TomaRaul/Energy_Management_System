package com.ds.ems.dtos;

import com.ds.ems.entities.Device;

import java.util.Objects;

public class DeviceDTO {
    private Integer id;
    private String name;
    private Integer mcv;
    private Integer user_id;

    public DeviceDTO(){}

    public DeviceDTO(Integer id, String name, Integer mcv, Integer user_id) {
        this.id = id;
        this.name = name;
        this.mcv = mcv;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getMCV() {
        return mcv;
    }

    public void setMCV(Integer mcv) {
        this.mcv = mcv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO that = (DeviceDTO) o;
        return mcv == that.mcv && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mcv);
    }
}
