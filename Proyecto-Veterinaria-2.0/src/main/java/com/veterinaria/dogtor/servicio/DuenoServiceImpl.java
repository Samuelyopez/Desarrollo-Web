package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.repositorio.DuenoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuenoServiceImpl implements DuenoService {

    private final DuenoRepository duenoRepository;
    private final MascotaService mascotaService;

    @Override
    @Transactional(readOnly = true)
    public List<Dueno> findAll() {
        return duenoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Dueno findById(Integer id) {
        return duenoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Dueno findByCorreo(String correo) {
        return duenoRepository.findByUsuario_Correo(correo).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dueno> findByNombreContaining(String nombre) {
        return duenoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional
    public Dueno save(Dueno dueno) {
        return duenoRepository.save(dueno);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // Las mascotas nunca se borran de la base de datos: si se elimina el dueño,
        // quedan sin dueño y se marcan inactivas ("en casa") en vez de eliminarse.
        Dueno dueno = duenoRepository.findById(id).orElse(null);
        if (dueno != null) {
            for (Mascota mascota : mascotaService.findByDueno(dueno)) {
                mascota.setDueno(null);
                mascota.setActiva(false);
                mascotaService.save(mascota);
            }
        }
        duenoRepository.deleteById(id);
    }
}
