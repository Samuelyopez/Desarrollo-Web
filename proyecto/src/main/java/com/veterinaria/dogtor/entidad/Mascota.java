package com.veterinaria.dogtor.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString(exclude = {"dueno"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@jakarta.persistence.Table(name = "mascota", indexes = {
    @jakarta.persistence.Index(name = "idx_mascota_dueno", columnList = "dueno_id"),
    @jakarta.persistence.Index(name = "idx_mascota_activo_adopcion", columnList = "activo, enAdopcion")
})
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    @jakarta.persistence.Column(nullable = false, length = 100)
    private String nombre;

    @jakarta.validation.constraints.NotBlank(message = "La especie es obligatoria")
    @jakarta.persistence.Column(nullable = false, length = 50)
    private String especie;

    @jakarta.persistence.Column(length = 50)
    private String raza;

    private Integer edad;

    @jakarta.validation.constraints.PositiveOrZero(message = "El peso no puede ser negativo")
    private Double peso;

    @jakarta.persistence.Column(length = 500)
    private String fotoUrl;

    @jakarta.persistence.OneToMany(mappedBy = "mascota", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<Vacuna> vacunas = new java.util.ArrayList<>();

    @jakarta.persistence.Column(length = 500)
    private String observaciones;

    @jakarta.persistence.Column(nullable = false)
    private boolean enAdopcion;

    @jakarta.persistence.Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "dueno_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Dueno dueno;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    @org.hibernate.annotations.UpdateTimestamp
    private java.time.LocalDateTime fechaActualizacion;
}


