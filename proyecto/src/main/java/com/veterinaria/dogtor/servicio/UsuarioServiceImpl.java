package com.veterinaria.dogtor.servicio;
import com.veterinaria.dogtor.errores.ResourceNotFoundException;

import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario authenticate(String correo, String password) {
        return usuarioRepository.findByCorreoAndActivoTrue(correo)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(password))
                .orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario searchById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado con id " + id));
    }

    @Override
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreoAndActivoTrue(correo).orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado con correo " + correo));
    }

    @Override
    public List<Usuario> searchAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
