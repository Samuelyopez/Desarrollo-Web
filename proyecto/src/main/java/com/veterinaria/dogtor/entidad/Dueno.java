package com.veterinaria.dogtor.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString(exclude = {"usuario"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false, length = 150)
    private String nombre;

    @jakarta.persistence.Column(length = 20)
    private String telefono;

    @jakarta.persistence.Column(length = 255)
    private String direccion;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @jakarta.persistence.Column(nullable = false)
    @lombok.Builder.Default
    private boolean activo = true;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    @org.hibernate.annotations.UpdateTimestamp
    private java.time.LocalDateTime fechaActualizacion;

    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    public void setCorreo(String correo) {
        if (this.usuario == null) {
            this.usuario = new Usuario();
        }
        this.usuario.setCorreo(correo);
    }

    public String getPassword() {
        return usuario != null ? usuario.getPassword() : null;
    }

    public void setPassword(String password) {
        if (this.usuario == null) {
            this.usuario = new Usuario();
        }
        this.usuario.setPassword(password);
    }

    public boolean isAdmin() {
        return usuario != null && com.veterinaria.dogtor.entidad.RolUsuario.ADMIN.equals(usuario.getRol());
    }

    public void setAdmin(boolean admin) {
        if (this.usuario == null) {
            this.usuario = new Usuario();
        }
        this.usuario.setRol(admin ? com.veterinaria.dogtor.entidad.RolUsuario.ADMIN : com.veterinaria.dogtor.entidad.RolUsuario.DUENO);
    }
}



