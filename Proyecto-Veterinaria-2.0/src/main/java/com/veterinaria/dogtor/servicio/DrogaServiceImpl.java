package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Droga;
import com.veterinaria.dogtor.repositorio.DrogaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrogaServiceImpl implements DrogaService {

    private final DrogaRepository drogaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Droga> findAll() {
        return drogaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Droga findById(Integer id) {
        return drogaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Droga save(Droga droga) {
        return drogaRepository.save(droga);
    }
}
