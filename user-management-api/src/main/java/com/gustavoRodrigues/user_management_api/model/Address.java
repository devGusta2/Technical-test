package com.gustavorodrigues.user_management_api.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter
@Table(name = "address")
public class Address extends Auditable{
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean main;
    private boolean isActive = true;

    //atribtos do pdf
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String neighborhood;

    private String complement;

    @ManyToOne 
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
