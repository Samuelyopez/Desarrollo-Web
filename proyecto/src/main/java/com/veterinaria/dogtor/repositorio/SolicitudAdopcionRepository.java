package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.SolicitudAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {
    @Transactional
    void deleteByDueno(Dueno dueno);

    @Transactional
    void deleteByMascota(Mascota mascota);
}
