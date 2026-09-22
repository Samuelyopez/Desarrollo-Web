package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findByCorreo(String correo);
    Usuario save(Usuario usuario);
    boolean authenticate(String correo, String password);
}
