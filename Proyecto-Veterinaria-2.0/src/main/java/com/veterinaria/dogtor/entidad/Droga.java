package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drogas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Droga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private String dosisRecomendada;
}
