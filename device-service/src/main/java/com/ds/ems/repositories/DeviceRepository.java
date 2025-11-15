package com.ds.ems.repositories;

import com.ds.ems.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    // JPA generate query by existing field
    List<Device> findByName(String name);

    @Query(value = "SELECT p " +
            "FROM Device p " +
            "WHERE p.name = :name " )
    Optional<Device> findDevicesByName(@Param("name") String name);

    @Query("SELECT d FROM Device d WHERE d.user_id = :user_id")
    List<Device> findDevicesByUserId(@Param("user_id") Integer user_id);

}
