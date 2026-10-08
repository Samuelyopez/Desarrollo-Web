package com.veterinaria.dogtor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.repository.MedicamentoRepository;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> listar(boolean soloDisponibles) {
        return soloDisponibles
                ? medicamentoRepository.findByUnidadesDisponiblesGreaterThanOrderByNombreAsc(0)
                : medicamentoRepository.findAllByOrderByNombreAsc();
    }
}
