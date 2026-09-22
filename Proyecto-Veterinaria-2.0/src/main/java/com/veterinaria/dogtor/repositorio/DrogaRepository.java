package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Droga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrogaRepository extends JpaRepository<Droga, Integer> {
}
