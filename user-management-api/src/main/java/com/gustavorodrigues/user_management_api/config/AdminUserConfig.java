package com.gustavorodrigues.user_management_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.gustavorodrigues.user_management_api.dto.UserDto;
import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.services.RoleService;
import com.gustavorodrigues.user_management_api.services.UserServices;

import org.springframework.transaction.annotation.Transactional;

@Configuration 
public class AdminUserConfig implements CommandLineRunner {
    
    private final RoleService roleService;
    private final UserServices userServices;
    public AdminUserConfig(RoleService roleService, UserServices userServices) {
        this.roleService = roleService;
        this.userServices = userServices;
    }



    @Override
    @Transactional
    public void run(String... args) {
        var adminRole = roleService.findOrCreate(RoleEnum.ADMIN.name());
        roleService.findOrCreate(RoleEnum.USER.name());

        var adminEmail = "admin@dellavolpe.com";
        if (userServices.fetchByEmail(adminEmail).isEmpty()) {
            var admin = new UserDto("admin", adminEmail, "123", "11111111111");
            userServices.createAdminUser(admin, adminRole);
        }
    }
}
