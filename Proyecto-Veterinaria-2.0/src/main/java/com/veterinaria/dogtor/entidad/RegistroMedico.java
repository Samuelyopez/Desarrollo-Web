package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registros_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 2000)
    private String diagnostico;

    @Column(length = 2000)
    private String tratamiento;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "veterinario_id")
    private Usuario veterinario;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "registro_medico_drogas",
            joinColumns = @JoinColumn(name = "registro_medico_id"),
            inverseJoinColumns = @JoinColumn(name = "droga_id"))
    private List<Droga> drogas = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        fecha = LocalDateTime.now();
    }
}
