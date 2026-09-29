package com.gustavorodrigues.user_management_api.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gustavorodrigues.user_management_api.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table(name = "users")
@RequiredArgsConstructor 
public class User extends Auditable {
    

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private boolean isActive; // usuario ativo/inativo

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<Address> address = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

  

}
