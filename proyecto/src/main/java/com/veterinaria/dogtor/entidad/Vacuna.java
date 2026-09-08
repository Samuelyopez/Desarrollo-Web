package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Vacuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    private LocalDate fechaAplicacion;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;
}
