package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.MascotaRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.MascotaRepository;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    // Reutiliza la búsqueda por cédula (404 si el dueño no existe)
    @Autowired
    private DuenoService duenoService;

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return mascotaRepository.findAllByOrderByNombreAsc();
        }
        return mascotaRepository.buscar(buscar.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Mascota buscarPorId(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe una mascota con el id " + id));
    }

    @Override
    @Transactional
    public Mascota crear(MascotaRequest request) {
        Dueno dueno = duenoService.buscarPorCedula(request.duenoCedula());
        String nombre = request.nombre().trim();
        validarNombreUnico(dueno, nombre, null);

        Mascota mascota = new Mascota(nombre, vacioANull(request.raza()), request.edad(), request.peso(),
                vacioANull(request.enfermedad()), vacioANull(request.foto()), true);
        dueno.agregarMascota(mascota);
        return mascotaRepository.save(mascota);
    }

    @Override
    @Transactional
    public Mascota actualizar(Long id, MascotaRequest request) {
        Mascota mascota = buscarPorId(id);
        Dueno dueno = duenoService.buscarPorCedula(request.duenoCedula());
        String nombre = request.nombre().trim();
        validarNombreUnico(dueno, nombre, mascota.getId());

        mascota.setNombre(nombre);
        mascota.setRaza(vacioANull(request.raza()));
        mascota.setEdad(request.edad());
        mascota.setPeso(request.peso());
        mascota.setEnfermedad(vacioANull(request.enfermedad()));
        mascota.setFoto(vacioANull(request.foto()));
        // Se permite corregir el dueño
        mascota.setDueno(dueno);
        // Al terminar la transacción JPA guarda los cambios (dirty checking)
        return mascota;
    }

    @Override
    @Transactional
    public Mascota cambiarEstado(Long id, boolean activa) {
        Mascota mascota = buscarPorId(id);
        mascota.setActiva(activa);
        return mascota;
    }

    // "Mascota ya registrada" = el mismo dueño ya tiene una mascota con ese nombre
    private void validarNombreUnico(Dueno dueno, String nombre, Long idActual) {
        mascotaRepository.findByDueno_IdAndNombreIgnoreCase(dueno.getId(), nombre)
                .filter(otra -> !otra.getId().equals(idActual))
                .ifPresent(otra -> {
                    throw new ConflictException(dueno.getNombre() + " ya tiene una mascota llamada " + otra.getNombre());
                });
    }

    private String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
