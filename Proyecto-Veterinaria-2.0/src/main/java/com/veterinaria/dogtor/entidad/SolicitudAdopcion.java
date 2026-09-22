package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitudes_adopcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombreAdoptante;

    @Column(nullable = false, length = 50)
    private String telefono;

    @Column(nullable = false, length = 200)
    private String direccion;

    private boolean tieneOtrasMascotas;

    @Column(length = 500)
    private String tiempoLibre;

    @Column(length = 1000)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;
}
