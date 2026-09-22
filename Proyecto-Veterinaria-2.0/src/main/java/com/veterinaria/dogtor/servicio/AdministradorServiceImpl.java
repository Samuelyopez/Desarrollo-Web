package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Administrador;
import com.veterinaria.dogtor.repositorio.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    @Override
    @Transactional
    public Administrador save(Administrador administrador) {
        return administradorRepository.save(administrador);
    }
}
