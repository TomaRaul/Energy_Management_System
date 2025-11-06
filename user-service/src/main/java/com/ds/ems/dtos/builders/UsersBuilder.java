package com.ds.ems.dtos.builders;

import com.ds.ems.dtos.UsersDTO;
import com.ds.ems.dtos.UsersDetailsDTO;
import com.ds.ems.entities.Users;


public class UsersBuilder {

    private UsersBuilder() {
    }

    public static UsersDTO toUsersDTO(Users Users) {
        return new UsersDTO(Users.getId(), Users.getName(), Users.getAge(), Users.getRole(), Users.getAddress(), Users.getEmail(), Users.getUsername());
    }

    public static UsersDetailsDTO toUsersDetailsDTO(Users Users) {
        return new UsersDetailsDTO(Users.getId(), Users.getName(), Users.getAge(), Users.getRole(), Users.getAddress(), Users.getEmail(), Users.getUsername());
    }

    public static Users toEntity(UsersDetailsDTO UsersDetailsDTO) {
        return new Users(UsersDetailsDTO.getName(),
                UsersDetailsDTO.getAddress(),
                UsersDetailsDTO.getAge(),
                UsersDetailsDTO.getRole(),
                UsersDetailsDTO.getEmail(),
                UsersDetailsDTO.getId(),
                UsersDetailsDTO.getUsername());
    }
}
