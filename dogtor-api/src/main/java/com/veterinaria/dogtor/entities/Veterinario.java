package com.veterinaria.dogtor.entities;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Médico veterinario. Su estado activo/inactivo (laboral) vive en Usuario.activo
@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@NoArgsConstructor
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false)
    private String nombre;

    private String especialidad;

    // URL de la foto
    private String foto;

    // Número de atenciones = tratamientos que ha dado. No se guarda: lo calcula la BD (subconsulta)
    @Formula("(select count(*) from tratamientos t where t.veterinario_id = id)")
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer cantidadAtenciones = 0;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Veterinario(String cedula, String nombre, String especialidad, String foto, Usuario usuario) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.foto = foto;
        this.usuario = usuario;
    }

    @JsonProperty(value = "correo", access = JsonProperty.Access.READ_ONLY)
    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    @JsonProperty(value = "activo", access = JsonProperty.Access.READ_ONLY)
    public boolean isActivo() {
        return usuario != null && usuario.isActivo();
    }
}
