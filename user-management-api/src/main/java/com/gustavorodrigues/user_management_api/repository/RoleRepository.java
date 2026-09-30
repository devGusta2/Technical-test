package com.gustavorodrigues.user_management_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gustavorodrigues.user_management_api.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    

    @Query("SELECT r FROM Role r WHERE r.name =:name")
    Optional<Role> findByName(@Param("name") String name);

}
