package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String raza;
    private String edad;
    
    @Column(length = 500)
    private String fotoUrl;
    
    private String vacunas;

    @Column(columnDefinition = "boolean default false")
    private boolean enAdopcion;

    @ManyToOne
    @JoinColumn(name = "dueno_id")
    private Dueno dueno;
}
