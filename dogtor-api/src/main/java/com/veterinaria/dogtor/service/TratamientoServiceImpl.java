package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tratamiento> historialDe(Long mascotaId) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("No existe una mascota con el id " + mascotaId);
        }
        return tratamientoRepository.findByMascota_IdOrderByFechaDescIdDesc(mascotaId);
    }
}
