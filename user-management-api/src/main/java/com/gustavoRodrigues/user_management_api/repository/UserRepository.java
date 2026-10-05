package com.gustavorodrigues.user_management_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gustavorodrigues.user_management_api.model.User;

@Repository 
public interface UserRepository extends JpaRepository <User, UUID> {
    

    @Query("SELECT usr FROM User usr WHERE usr.email =:email")
    Optional<User> findByEmail(@Param("email") String email);


    boolean existsByEmail(String email);

    Optional<User> findByIdAndActiveTrue(UUID id);
}
