package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByDuenoAndActivoTrue(Dueno dueno);

    List<Mascota> findByEnAdopcionAndActivoTrue(boolean enAdopcion);

    List<Mascota> findByActivoTrue();

    List<Mascota> findAllByOrderByActivoDescIdAsc();

    org.springframework.data.domain.Page<Mascota> findByActivoTrue(org.springframework.data.domain.Pageable pageable);

    List<Mascota> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
