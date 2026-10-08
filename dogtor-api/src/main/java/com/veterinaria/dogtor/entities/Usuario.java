package com.veterinaria.dogtor.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Credenciales y rol de acceso. Dueno, Veterinario y Administrador tienen cada uno su Usuario
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    // Se puede recibir en el JSON pero nunca se devuelve
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Para veterinarios indica si está trabajando (vacaciones/incapacidad = inactivo)
    private boolean activo = true;

    public Usuario(String correo, String password, Rol rol) {
        this.correo = correo.trim().toLowerCase();
        this.password = password;
        this.rol = rol;
    }
}
