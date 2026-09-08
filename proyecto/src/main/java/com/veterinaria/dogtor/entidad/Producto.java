package com.veterinaria.dogtor.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false, length = 150)
    private String nombre;

    @jakarta.persistence.Column(length = 500)
    private String descripcion;

    @jakarta.persistence.Column(nullable = false)
    private Double precio;

    @jakarta.persistence.Column(length = 500)
    private String fotoUrl;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(nullable = false, length = 50)
    private CategoriaProducto categoria;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    @org.hibernate.annotations.UpdateTimestamp
    private java.time.LocalDateTime fechaActualizacion;
}


