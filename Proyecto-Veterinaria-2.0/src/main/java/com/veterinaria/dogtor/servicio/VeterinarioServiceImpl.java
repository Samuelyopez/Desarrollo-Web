package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.entidad.Veterinario;
import com.veterinaria.dogtor.repositorio.UsuarioRepository;
import com.veterinaria.dogtor.repositorio.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario findById(Integer id) {
        return veterinarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario findByCorreo(String correo) {
        return veterinarioRepository.findByUsuario_Correo(correo).orElse(null);
    }

    @Override
    @Transactional
    public Veterinario save(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        veterinarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void sincronizarConUsuarios() {
        // Crea el perfil de Veterinario para usuarios con rol VETERINARIO que quedaron
        // sin él (bases de datos de antes de que esta tabla existiera).
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getRol() == RolUsuario.VETERINARIO
                    && veterinarioRepository.findByUsuario_Correo(usuario.getCorreo()).isEmpty()) {
                veterinarioRepository.save(Veterinario.builder().usuario(usuario).build());
            }
        }
    }
}
