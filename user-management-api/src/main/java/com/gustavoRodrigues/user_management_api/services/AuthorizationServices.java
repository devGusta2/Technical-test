package com.gustavorodrigues.user_management_api.services;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gustavorodrigues.user_management_api.repository.AddressRepository;

@Service
public class AuthorizationServices {

    private final AddressRepository addressRepository;

    public AuthorizationServices(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_ADMIN"));
    }

    public UUID getAuthenticatedUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    public boolean isOwnerAddress(UUID addresId, Authentication authentication) {
        UUID authenticatedUserId = getAuthenticatedUserId(authentication);

        return addressRepository.findByIdAndActiveTrue(addresId)
                .map(ad -> ad.getUser().getId().equals(authenticatedUserId))
                .orElse(false);
    }

    public boolean isOwnerOfUser(
            UUID userId,
            Authentication authentication) {

        return userId.equals(
                getAuthenticatedUserId(authentication));
    }

}
