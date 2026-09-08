package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "duenos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 100)
    private String password;

    // Relación OneToMany con Mascotas
    // Omitiendo mappedBy bidireccional si no es estrictamente necesario, o podemos ponerlo
    // @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL)
    // private List<Mascota> mascotas;
}
