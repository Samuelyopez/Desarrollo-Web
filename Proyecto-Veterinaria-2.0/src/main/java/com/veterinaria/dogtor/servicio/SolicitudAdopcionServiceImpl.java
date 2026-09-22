package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.SolicitudAdopcion;
import com.veterinaria.dogtor.repositorio.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    private final SolicitudAdopcionRepository repository;

    @Override
    @Transactional
    public SolicitudAdopcion save(SolicitudAdopcion solicitud) {
        return repository.save(solicitud);
    }
}
