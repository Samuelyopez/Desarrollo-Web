package com.veterinaria.dogtor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Dueno_Id (con guion bajo) navega la relación; sin él choca con el getter Mascota.getDuenoId()
    List<Mascota> findByDueno_Id(Long duenoId);

    List<Mascota> findByActiva(boolean activa);

    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
}
