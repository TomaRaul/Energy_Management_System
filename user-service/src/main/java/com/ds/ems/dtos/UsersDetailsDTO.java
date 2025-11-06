package com.ds.ems.dtos;

import com.ds.ems.dtos.validators.annotation.AgeLimit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class UsersDetailsDTO {

    private int id;

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "address is required")
    private String address;
    @NotNull(message = "age is required")
    @AgeLimit(value = 18)
    private Integer age;
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "role is required")
    private String role;
    @NotBlank(message = "username is required")
    private String username;

    public UsersDetailsDTO() {}

    public UsersDetailsDTO(Integer id, String name, Integer age, String role, String address, String email, String username) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.email = email;
        this.role = role;
        this.username = username;
    }

    public UsersDetailsDTO(int id, String name, String address, int age, String email, String role, String username) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.email = email;
        this.role = role;
        this.username = username;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersDetailsDTO that = (UsersDetailsDTO) o;
        return age == that.age && Objects.equals(name, that.name) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, age);
    }

}
