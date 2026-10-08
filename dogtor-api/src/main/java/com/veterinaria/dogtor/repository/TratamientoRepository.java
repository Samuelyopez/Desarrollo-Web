package com.veterinaria.dogtor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Tratamiento;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByMascotaId(Long mascotaId);

    List<Tratamiento> findByMascota_Dueno_Id(Long duenoId);

    List<Tratamiento> findByVeterinarioId(Long veterinarioId);
}
