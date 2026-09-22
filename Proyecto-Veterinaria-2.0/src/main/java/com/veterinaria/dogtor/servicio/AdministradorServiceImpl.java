package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Administrador;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.repositorio.AdministradorRepository;
import com.veterinaria.dogtor.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    @Override
    @Transactional
    public Administrador save(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @Override
    @Transactional
    public void sincronizarConUsuarios() {
        // Crea el perfil de Administrador para usuarios con rol ADMIN que quedaron
        // sin él (bases de datos de antes de que esta tabla existiera).
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getRol() == RolUsuario.ADMIN
                    && administradorRepository.findByUsuario_Correo(usuario.getCorreo()).isEmpty()) {
                administradorRepository.save(Administrador.builder().usuario(usuario).build());
            }
        }
    }
}
