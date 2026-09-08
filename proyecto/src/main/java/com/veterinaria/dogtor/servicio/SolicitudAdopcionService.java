package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.SolicitudAdopcion;

public interface SolicitudAdopcionService {
    SolicitudAdopcion save(SolicitudAdopcion solicitud);
    java.util.List<SolicitudAdopcion> searchAll();
    SolicitudAdopcion searchById(Long id);
    void actualizarEstado(Long id, com.veterinaria.dogtor.entidad.EstadoSolicitud nuevoEstado);
}

