package com.veterinaria.dogtor.entities;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Cliente de la veterinaria: dueño de una o varias mascotas
@Entity
@Table(name = "duenos")
@Getter
@Setter
@NoArgsConstructor
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false)
    private String nombre;

    private String celular;

    // Calculado por la BD (subconsulta): la tabla del front muestra cuántas mascotas tiene
    // sin pedir la lista LAZY ni hacer una petición por dueño
    @Formula("(select count(*) from mascotas m where m.dueno_id = id)")
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer cantidadMascotas = 0;

    // Al borrar el dueño se borra su usuario
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // Al borrar el dueño se borran sus mascotas (eliminación en cascada)
    @JsonIgnore
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    public Dueno(String cedula, String nombre, String celular, Usuario usuario) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.celular = celular;
        this.usuario = usuario;
    }

    // El correo vive en Usuario, pero se muestra como dato del dueño
    @JsonProperty(value = "correo", access = JsonProperty.Access.READ_ONLY)
    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    public void agregarMascota(Mascota mascota) {
        mascota.setDueno(this);
        mascotas.add(mascota);
    }
}
