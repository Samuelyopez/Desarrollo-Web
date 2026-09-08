package com.veterinaria.dogtor.servicio;
import com.veterinaria.dogtor.errores.ResourceNotFoundException;

import com.veterinaria.dogtor.entidad.SolicitudAdopcion;
import com.veterinaria.dogtor.repositorio.SolicitudAdopcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    @Autowired
    private SolicitudAdopcionRepository repository;

    @Autowired
    private com.veterinaria.dogtor.repositorio.MascotaRepository mascotaRepository;

    @Override
    public SolicitudAdopcion save(SolicitudAdopcion solicitud) {
        return repository.save(solicitud);
    }
    @Override
    public java.util.List<SolicitudAdopcion> searchAll() {
        return repository.findAll();
    }

    @Override
    public SolicitudAdopcion searchById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + id));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void actualizarEstado(Long id, com.veterinaria.dogtor.entidad.EstadoSolicitud nuevoEstado) {
        SolicitudAdopcion solicitud = searchById(id);
        solicitud.setEstado(nuevoEstado);
        if (com.veterinaria.dogtor.entidad.EstadoSolicitud.APROBADA.equals(nuevoEstado)) {
            com.veterinaria.dogtor.entidad.Mascota mascota = solicitud.getMascota();
            mascota.setEnAdopcion(false);
            mascota.setDueno(solicitud.getDueno());
            mascotaRepository.save(mascota);
        }
        repository.save(solicitud);
    }
}
