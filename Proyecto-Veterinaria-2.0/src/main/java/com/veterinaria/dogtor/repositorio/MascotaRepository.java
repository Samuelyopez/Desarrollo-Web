package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    List<Mascota> findByDueno(Dueno dueno);
    List<Mascota> findByEnAdopcion(boolean enAdopcion);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
}
