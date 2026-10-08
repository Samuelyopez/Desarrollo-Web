package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> pacientesDe(Long veterinarioId) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new NotFoundException("No existe un veterinario con el id " + veterinarioId);
        }
        return tratamientoRepository.pacientesDe(veterinarioId);
    }
}
