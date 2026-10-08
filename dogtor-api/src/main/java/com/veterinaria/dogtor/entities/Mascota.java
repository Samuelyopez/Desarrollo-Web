package com.veterinaria.dogtor.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Perro o gato hospitalizado. No se borra: se activa (en la veterinaria) o desactiva (en casa).
// No tiene lista de tratamientos a propósito: así ninguna cascada llega a Tratamiento.
@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String raza;

    // En años
    private Integer edad;

    // En kilogramos
    private Double peso;

    // Puede estar vacía mientras un veterinario la atiende
    private String enfermedad;

    // URL de la foto
    private String foto;

    private boolean activa = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;

    public Mascota(String nombre, String raza, Integer edad, Double peso, String enfermedad, String foto, boolean activa) {
        this.nombre = nombre;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.enfermedad = enfermedad;
        this.foto = foto;
        this.activa = activa;
    }

    // Solo el id del dueño: el front pide los datos del dueño en otra petición
    @JsonProperty(value = "duenoId", access = JsonProperty.Access.READ_ONLY)
    public Long getDuenoId() {
        return dueno != null ? dueno.getId() : null;
    }
}
