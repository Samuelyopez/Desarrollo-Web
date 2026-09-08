package com.veterinaria.dogtor.servicio;
import com.veterinaria.dogtor.errores.ResourceNotFoundException;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.repositorio.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private com.veterinaria.dogtor.repositorio.SolicitudAdopcionRepository solicitudAdopcionRepository;

    @Override
    public List<Mascota> searchAll() {
        return mascotaRepository.findByActivoTrue();
    }

    @Override
    public Mascota searchById(Long id) {
        return mascotaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado con id " + id));
    }
    
    @Override
    public List<Mascota> findByDueno(Dueno dueno) {
        return mascotaRepository.findByDuenoAndActivoTrue(dueno);
    }

    @Override
    public List<Mascota> findByEnAdopcion(boolean enAdopcion) {
        return mascotaRepository.findByEnAdopcionAndActivoTrue(enAdopcion);
    }

    @Override
    public Mascota save(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public List<Mascota> searchAllIncluyendoInactivas() {
        return mascotaRepository.findAllByOrderByActivoDescIdAsc();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        // Obsolete, left to compile if needed, toggleActivo is preferred
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void toggleActivo(Long id) {
        Mascota mascota = searchById(id);
        mascota.setActivo(!mascota.isActivo());
        if (!mascota.isActivo()) {
            mascota.setEnAdopcion(false);
        }
        mascotaRepository.save(mascota);
    }
}
