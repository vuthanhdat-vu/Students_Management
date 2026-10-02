package com.StudentManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registration_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSetting {

    @Id
    private Long id;

    @Column(nullable = false)
    private boolean open;
}