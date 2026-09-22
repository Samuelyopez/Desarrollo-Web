package com.veterinaria.dogtor.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private String telefono;

    private String direccion;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @PrePersist
    protected void alCrear() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    protected void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    public boolean isAdmin() {
        return usuario != null && RolUsuario.ADMIN.equals(usuario.getRol());
    }
}
