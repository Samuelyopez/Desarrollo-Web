package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Integer> {
    Optional<Dueno> findByUsuario_Correo(String correo);
    List<Dueno> findByNombreContainingIgnoreCase(String nombre);
}
