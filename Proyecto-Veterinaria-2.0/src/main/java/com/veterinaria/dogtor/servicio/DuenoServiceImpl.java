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
        // Al eliminar un dueño se elimina también la información de sus mascotas
        // (y sus registros médicos, en cascada vía Mascota.registros).
        Dueno dueno = duenoRepository.findById(id).orElse(null);
        if (dueno != null) {
            for (Mascota mascota : mascotaService.findByDueno(dueno)) {
                mascotaService.delete(mascota.getId());
            }
        }
        duenoRepository.deleteById(id);
    }
}
