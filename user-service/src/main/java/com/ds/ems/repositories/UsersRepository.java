package com.ds.ems.repositories;

import com.ds.ems.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    /**
     * Example: JPA generate query by existing field
     */
    List<Users> findByName(String name);

    /**
     * Example: Custom query
     */
    @Query(value = "SELECT p " +
            "FROM Users p " +
            "WHERE p.name = :name " )
    Optional<Users> findSeniorsByName(@Param("name") String name);

}
