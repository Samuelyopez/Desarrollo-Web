package com.veterinaria.dogtor.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Droga que se suministra en un tratamiento. Se carga desde el Excel al iniciar la API
@Entity
@Table(name = "medicamentos")
@Getter
@Setter
@NoArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Dato interno para calcular ganancias: no se envía al front
    @JsonIgnore
    private Double precioCompra;

    private Double precioVenta;

    private Integer unidadesDisponibles = 0;

    private Integer unidadesVendidas = 0;

    public Medicamento(String nombre, Double precioCompra, Double precioVenta, Integer unidadesDisponibles, Integer unidadesVendidas) {
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.unidadesDisponibles = unidadesDisponibles;
        this.unidadesVendidas = unidadesVendidas;
    }
}
