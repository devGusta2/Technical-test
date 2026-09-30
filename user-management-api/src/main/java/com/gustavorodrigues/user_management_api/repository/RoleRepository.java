package com.gustavorodrigues.user_management_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavorodrigues.user_management_api.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
