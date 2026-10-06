package com.gustavorodrigues.user_management_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gustavorodrigues.user_management_api.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByIsActiveTrue();

    List<Address> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<Address> findByIdAndIsActiveTrue(UUID id);

    Optional<Address> findByUserIdAndMainTrueAndIsActiveTrue(UUID userId);
}
