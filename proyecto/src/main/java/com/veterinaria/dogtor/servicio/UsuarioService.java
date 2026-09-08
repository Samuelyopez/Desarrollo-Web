package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario authenticate(String correo, String password);

    Usuario save(Usuario usuario);

    Usuario searchById(Long id);

    Usuario findByCorreo(String correo);

    List<Usuario> searchAll();

    void delete(Long id);
}
