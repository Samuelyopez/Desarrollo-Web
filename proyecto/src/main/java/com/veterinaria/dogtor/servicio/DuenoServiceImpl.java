package com.veterinaria.dogtor.servicio;
import com.veterinaria.dogtor.errores.ResourceNotFoundException;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.repositorio.DuenoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DuenoServiceImpl implements DuenoService {

    @Autowired
    private DuenoRepository duenoRepository;
    
    @Autowired
    private com.veterinaria.dogtor.repositorio.UsuarioRepository usuarioRepository;

    @Autowired
    private com.veterinaria.dogtor.repositorio.MascotaRepository mascotaRepository;

    @Override
    public List<Dueno> searchAll() {
        return duenoRepository.findByActivoTrue();
    }

    @Override
    public Dueno searchById(Long id) {
        return duenoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado con id " + id));
    }

    @Override
    public Dueno save(Dueno dueno) {
        if (dueno.getUsuario() != null) {
            if (dueno.getUsuario().getRol() == null) {
                dueno.getUsuario().setRol(com.veterinaria.dogtor.entidad.RolUsuario.DUENO);
            }
            com.veterinaria.dogtor.entidad.Usuario usuarioGuardado = usuarioRepository.save(dueno.getUsuario());
            dueno.setUsuario(usuarioGuardado);
        }
        return duenoRepository.save(dueno);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        Dueno dueno = duenoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado: " + id));

        dueno.setActivo(false);
        if (dueno.getUsuario() != null) {
            dueno.getUsuario().setActivo(false);
        }
        duenoRepository.save(dueno);

        // desactivar sus mascotas también, en vez de borrarlas
        List<com.veterinaria.dogtor.entidad.Mascota> mascotas = mascotaRepository.findByDuenoAndActivoTrue(dueno);
        mascotas.forEach(m -> { m.setActivo(false); m.setEnAdopcion(false); });
        mascotaRepository.saveAll(mascotas);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void toggleActivo(Long id) {
        Dueno dueno = searchById(id);
        boolean nuevoEstado = !dueno.isActivo();
        dueno.setActivo(nuevoEstado);
        if (dueno.getUsuario() != null) {
            dueno.getUsuario().setActivo(nuevoEstado);
        }
        duenoRepository.save(dueno);
        
        if (!nuevoEstado) {
            List<com.veterinaria.dogtor.entidad.Mascota> mascotas = mascotaRepository.findByDuenoAndActivoTrue(dueno);
            mascotas.forEach(m -> { m.setActivo(false); m.setEnAdopcion(false); });
            mascotaRepository.saveAll(mascotas);
        }
    }

    @Override
    public List<Dueno> searchAllIncluyendoInactivos() {
        return duenoRepository.findAllByOrderByActivoDescIdAsc();
    }
}
