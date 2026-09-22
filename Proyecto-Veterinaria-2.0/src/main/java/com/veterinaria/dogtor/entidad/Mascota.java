package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activa = true;

    @ManyToOne
    @JoinColumn(name = "dueno_id")
    private Dueno dueno;

    @Builder.Default
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroMedico> registros = new ArrayList<>();
}
