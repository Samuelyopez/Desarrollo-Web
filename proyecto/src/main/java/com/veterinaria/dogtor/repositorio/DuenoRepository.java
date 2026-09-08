package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    @Query("SELECT d FROM Dueno d WHERE LOWER(d.usuario.correo) = LOWER(:correo) AND d.activo = true")
    Optional<Dueno> findByCorreo(@Param("correo") String correo);

    java.util.List<Dueno> findByActivoTrue();
    java.util.List<Dueno> findAllByOrderByActivoDescIdAsc();
}
