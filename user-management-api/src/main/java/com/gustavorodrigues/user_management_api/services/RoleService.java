package com.gustavorodrigues.user_management_api.services;

import org.springframework.stereotype.Service;

import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.repository.RoleRepository;

@Service 
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findOrCreate(String name){
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }
}
