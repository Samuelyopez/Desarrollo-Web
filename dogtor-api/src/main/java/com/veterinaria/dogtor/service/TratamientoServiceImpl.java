package com.veterinaria.dogtor.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tratamiento> historialDe(Long mascotaId) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("No existe una mascota con el id " + mascotaId);
        }
        return tratamientoRepository.findByMascota_IdOrderByFechaDescIdDesc(mascotaId);
    }

    @Override
    @Transactional
    public Tratamiento crear(TratamientoRequest request) {
        Mascota mascota = mascotaRepository.findById(request.mascotaId())
                .orElseThrow(() -> new NotFoundException("No existe una mascota con el id " + request.mascotaId()));
        if (!mascota.isActiva()) {
            throw new ConflictException("Solo se puede dar tratamiento a mascotas activas ("
                    + mascota.getNombre() + " está en casa)");
        }

        // Sin sesión cualquiera puede llamar la API: se valida también que el veterinario esté activo
        Veterinario veterinario = veterinarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new NotFoundException("No existe un veterinario con el id " + request.veterinarioId()));
        if (!veterinario.isActivo()) {
            throw new ConflictException("El veterinario está inactivo y no puede dar tratamientos");
        }

        // Bloquea el medicamento hasta el final de la transacción para no vender unidades que ya no hay
        Medicamento medicamento = medicamentoRepository.findByIdParaActualizar(request.medicamentoId())
                .orElseThrow(() -> new NotFoundException("No existe un medicamento con el id " + request.medicamentoId()));
        int disponibles = medicamento.getUnidadesDisponibles();
        if (disponibles < request.cantidad()) {
            throw new ConflictException(disponibles == 0
                    ? "No quedan unidades de " + medicamento.getNombre()
                    : "Solo quedan " + disponibles + " unidades de " + medicamento.getNombre());
        }

        medicamento.setUnidadesDisponibles(disponibles - request.cantidad());
        medicamento.setUnidadesVendidas(medicamento.getUnidadesVendidas() + request.cantidad());
        return tratamientoRepository.save(
                new Tratamiento(LocalDate.now(), request.cantidad(), mascota, veterinario, medicamento));
    }
}
