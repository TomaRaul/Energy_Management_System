package com.ds.ems.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "\"Device\"")
public class Device {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "mcv")
    private Integer mcv;

    @Column(name = "user_id")
    private Integer user_id;

    public Device() {
    }

    public Device(Integer id, String name, Integer mcv, Integer user_id) {
        this.id = id;
        this.name = name;
        this.mcv = mcv;
        this.user_id = user_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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


}