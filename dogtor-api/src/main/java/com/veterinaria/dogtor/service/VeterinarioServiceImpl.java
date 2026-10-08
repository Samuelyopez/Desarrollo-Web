package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.dto.VeterinarioRequest;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.BadRequestException;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.UsuarioRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> listar(String buscar, Boolean activo) {
        String texto = buscar == null ? "" : buscar.trim();
        return veterinarioRepository.buscar(texto, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario buscarPorCedula(String cedula) {
        return veterinarioRepository.findByCedula(cedula.trim())
                .orElseThrow(() -> new NotFoundException("No existe un veterinario con la cédula " + cedula));
    }

    @Override
    @Transactional
    public Veterinario crear(VeterinarioRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        String cedula = request.cedula().trim();
        if (veterinarioRepository.existsByCedula(cedula)) {
            throw new ConflictException("Ya existe un veterinario con la cédula " + cedula);
        }
        String correo = normalizarCorreo(request.correo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictException("El correo " + correo + " ya está registrado");
        }

        // La cascada guarda también el usuario (nace activo)
        Usuario usuario = new Usuario(correo, request.password(), Rol.VETERINARIO);
        Veterinario veterinario = new Veterinario(cedula, request.nombre().trim(), request.especialidad().trim(),
                vacioANull(request.foto()), usuario);
        return veterinarioRepository.save(veterinario);
    }

    @Override
    @Transactional
    public Veterinario actualizar(String cedula, VeterinarioRequest request) {
        Veterinario veterinario = buscarPorCedula(cedula);
        if (!veterinario.getCedula().equals(request.cedula().trim())) {
            throw new BadRequestException("La cédula no se puede modificar");
        }

        Usuario usuario = veterinario.getUsuario();
        String correo = normalizarCorreo(request.correo());
        usuarioRepository.findByCorreo(correo)
                .filter(otro -> !otro.getId().equals(usuario.getId()))
                .ifPresent(otro -> {
                    throw new ConflictException("El correo " + correo + " ya está registrado");
                });

        veterinario.setNombre(request.nombre().trim());
        veterinario.setEspecialidad(request.especialidad().trim());
        veterinario.setFoto(vacioANull(request.foto()));
        usuario.setCorreo(correo);
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(request.password());
        }
        // Al terminar la transacción JPA guarda los cambios (dirty checking)
        return veterinario;
    }

    @Override
    @Transactional
    public Veterinario cambiarEstado(String cedula, boolean activo) {
        Veterinario veterinario = buscarPorCedula(cedula);
        veterinario.getUsuario().setActivo(activo);
        return veterinario;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> pacientesDe(Long veterinarioId) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new NotFoundException("No existe un veterinario con el id " + veterinarioId);
        }
        return tratamientoRepository.pacientesDe(veterinarioId);
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    private String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
