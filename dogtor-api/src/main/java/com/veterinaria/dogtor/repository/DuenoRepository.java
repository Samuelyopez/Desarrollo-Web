package com.veterinaria.dogtor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Dueno;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    Optional<Dueno> findByCedula(String cedula);

    Optional<Dueno> findByUsuarioId(Long usuarioId);

    boolean existsByCedula(String cedula);
}
