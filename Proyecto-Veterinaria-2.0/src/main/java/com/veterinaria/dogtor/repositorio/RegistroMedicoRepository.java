package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RegistroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroMedicoRepository extends JpaRepository<RegistroMedico, Integer> {
    List<RegistroMedico> findByMascotaOrderByFechaDesc(Mascota mascota);
}
