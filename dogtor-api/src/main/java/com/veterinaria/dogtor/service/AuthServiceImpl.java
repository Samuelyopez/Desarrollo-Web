package com.veterinaria.dogtor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.LoginRequest;
import com.veterinaria.dogtor.dto.LoginResponse;
import com.veterinaria.dogtor.entities.Administrador;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.CredencialesInvalidasException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.errors.UsuarioInactivoException;
import com.veterinaria.dogtor.repository.AdministradorRepository;
import com.veterinaria.dogtor.repository.DuenoRepository;
import com.veterinaria.dogtor.repository.UsuarioRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

@Service
public class AuthServiceImpl implements AuthService {

    // Mismo mensaje si el correo no existe o la contraseña falla: no revela qué correos están registrados
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.correo().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException(CREDENCIALES_INVALIDAS));

        if (!usuario.getPassword().equals(request.password())) {
            throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
        }
        if (!usuario.isActivo()) {
            throw new UsuarioInactivoException("Tu usuario está inactivo, contacta al administrador");
        }

        // El nombre y el perfilId salen del perfil asociado al rol
        return switch (usuario.getRol()) {
            case DUENO -> {
                Dueno dueno = duenoRepository.findByUsuarioId(usuario.getId())
                        .orElseThrow(() -> perfilNoEncontrado());
                yield respuesta(usuario, dueno.getNombre(), dueno.getId());
            }
            case VETERINARIO -> {
                Veterinario veterinario = veterinarioRepository.findByUsuarioId(usuario.getId())
                        .orElseThrow(() -> perfilNoEncontrado());
                yield respuesta(usuario, veterinario.getNombre(), veterinario.getId());
            }
            case ADMIN -> {
                Administrador admin = administradorRepository.findByUsuarioId(usuario.getId())
                        .orElseThrow(() -> perfilNoEncontrado());
                yield respuesta(usuario, admin.getNombre(), admin.getId());
            }
        };
    }

    private LoginResponse respuesta(Usuario usuario, String nombre, Long perfilId) {
        return new LoginResponse(usuario.getId(), nombre, usuario.getCorreo(), usuario.getRol(), perfilId);
    }

    private NotFoundException perfilNoEncontrado() {
        return new NotFoundException("El usuario no tiene un perfil asociado");
    }
}
