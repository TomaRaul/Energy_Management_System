package com.ds.ems.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class DeviceDetailsDTO {

    private int id;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "mcv is required")
    private Integer mcv;

    @NotNull(message = "user id is required")
    private Integer user_id;


    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(Integer id, String name, Integer mcv, Integer user_id) {
        this.id = id;
        this.name = name;
        this.mcv = mcv;
        this.user_id = user_id;
    }

    public DeviceDetailsDTO(int id, String name, String address, int mcv, int user_id) {
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

    public Integer getMCV() {
        return mcv;
    }

    public void setMCV(Integer mcv) {
        this.mcv = mcv;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return mcv == that.mcv && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mcv);
    }

}
