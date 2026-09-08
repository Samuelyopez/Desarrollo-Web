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
@ToString(exclude = {"dueno", "mascota"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreAdoptante;

    private String telefono;

    private String direccion;

    private boolean tieneOtrasMascotas;

    private String tiempoLibre;

    private String motivo;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "dueno_id")
    private Dueno dueno;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    @org.hibernate.annotations.UpdateTimestamp
    private java.time.LocalDateTime fechaActualizacion;
}


