package com.gustavorodrigues.user_management_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavorodrigues.user_management_api.model.User;

public interface UserRepository extends JpaRepository <User, UUID> {
    
}
