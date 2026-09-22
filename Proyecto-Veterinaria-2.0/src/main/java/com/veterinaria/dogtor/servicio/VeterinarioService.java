package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Veterinario;
import java.util.List;

public interface VeterinarioService {
    List<Veterinario> findAll();
    Veterinario findById(Integer id);
    Veterinario findByCorreo(String correo);
    Veterinario save(Veterinario veterinario);
    void delete(Integer id);
    void sincronizarConUsuarios();
}
