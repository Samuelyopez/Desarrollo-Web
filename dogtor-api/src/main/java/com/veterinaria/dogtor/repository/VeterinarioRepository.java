package com.veterinaria.dogtor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByCedula(String cedula);

    Optional<Veterinario> findByUsuarioId(Long usuarioId);

    boolean existsByCedula(String cedula);
}
