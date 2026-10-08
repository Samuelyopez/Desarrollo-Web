package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.DuenoRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.errors.BadRequestException;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.DuenoRepository;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.UsuarioRepository;

@Service
public class DuenoServiceImpl implements DuenoService {

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Dueno> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return duenoRepository.findAll(Sort.by("nombre"));
        }
        return duenoRepository.buscar(buscar.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Dueno buscarPorCedula(String cedula) {
        return duenoRepository.findByCedula(cedula.trim())
                .orElseThrow(() -> new NotFoundException("No existe un dueño con la cédula " + cedula));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> mascotasDe(String cedula) {
        return mascotaRepository.findByDueno_Id(buscarPorCedula(cedula).getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Mascota mascotaDe(String cedula, Long mascotaId) {
        Dueno dueno = buscarPorCedula(cedula);
        return mascotaRepository.findByIdAndDueno_Id(mascotaId, dueno.getId())
                .orElseThrow(() -> new NotFoundException(dueno.getNombre() + " no tiene una mascota con el id " + mascotaId));
    }

    @Override
    @Transactional
    public Dueno crear(DuenoRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        String cedula = request.cedula().trim();
        if (duenoRepository.existsByCedula(cedula)) {
            throw new ConflictException("Ya existe un dueño con la cédula " + cedula);
        }
        String correo = normalizarCorreo(request.correo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictException("El correo " + correo + " ya está registrado");
        }

        // La cascada guarda también el usuario
        Usuario usuario = new Usuario(correo, request.password(), Rol.DUENO);
        Dueno dueno = new Dueno(cedula, request.nombre().trim(), request.celular().trim(), usuario);
        return duenoRepository.save(dueno);
    }

    @Override
    @Transactional
    public Dueno actualizar(String cedula, DuenoRequest request) {
        Dueno dueno = buscarPorCedula(cedula);
        if (!dueno.getCedula().equals(request.cedula().trim())) {
            throw new BadRequestException("La cédula no se puede modificar");
        }

        Usuario usuario = dueno.getUsuario();
        String correo = normalizarCorreo(request.correo());
        usuarioRepository.findByCorreo(correo)
                .filter(otro -> !otro.getId().equals(usuario.getId()))
                .ifPresent(otro -> {
                    throw new ConflictException("El correo " + correo + " ya está registrado");
                });

        dueno.setNombre(request.nombre().trim());
        dueno.setCelular(request.celular().trim());
        usuario.setCorreo(correo);
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(request.password());
        }
        // Al terminar la transacción JPA guarda los cambios (dirty checking)
        return dueno;
    }

    @Override
    @Transactional
    public void eliminar(String cedula) {
        Dueno dueno = buscarPorCedula(cedula);

        // 1. El historial de tratamientos se conserva: se desliga de las mascotas
        //    (el nombre de la mascota y los precios ya están copiados en cada tratamiento)
        List<Tratamiento> tratamientos = tratamientoRepository.findByMascota_Dueno_Id(dueno.getId());
        tratamientos.forEach(tratamiento -> tratamiento.setMascota(null));

        // 2. Se escriben esos UPDATE antes de cualquier DELETE para no violar la llave foránea
        tratamientoRepository.flush();

        // 3. La cascada borra sus mascotas y su usuario
        duenoRepository.delete(dueno);
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}
