package com.gustavorodrigues.user_management_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gustavorodrigues.user_management_api.model.User;

@Repository 
public interface UserRepository extends JpaRepository <User, UUID> {
    
}
