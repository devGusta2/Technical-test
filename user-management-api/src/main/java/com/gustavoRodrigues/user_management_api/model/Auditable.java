package com.gustavorodrigues.user_management_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@MappedSuperclass 
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @Column(nullable = false)
    @CreatedDate 
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @CreatedBy 
    private UUID createdBy;

    @LastModifiedDate 
    private LocalDateTime updatedAt;
    
    @LastModifiedBy 
    private UUID updatedBy;
}
