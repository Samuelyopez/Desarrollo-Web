package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByUsuario_Correo(String correo);
}
