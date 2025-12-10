package com.ds.ems.repositories;

import com.ds.ems.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, Long> {

    Optional<HourlyConsumption> findByDeviceIdAndHourTimestamp(Long deviceId, LocalDateTime hourTimestamp);

    List<HourlyConsumption> findByDeviceIdAndHourTimestampBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT h FROM HourlyConsumption h WHERE h.deviceId = :deviceId " +
            "AND DATE(h.hourTimestamp) = DATE(:date) ORDER BY h.hourTimestamp")
    List<HourlyConsumption> findByDeviceIdAndDate(
            @Param("deviceId") Long deviceId,
            @Param("date") LocalDate date
    );

    List<HourlyConsumption> findByDeviceIdOrderByHourTimestampDesc(Long deviceId);
}