package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RegistroMedico;
import com.veterinaria.dogtor.repositorio.RegistroMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroMedicoServiceImpl implements RegistroMedicoService {

    private final RegistroMedicoRepository registroMedicoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RegistroMedico> findByMascota(Mascota mascota) {
        return registroMedicoRepository.findByMascotaOrderByFechaDesc(mascota);
    }

    @Override
    @Transactional
    public RegistroMedico save(RegistroMedico registro) {
        return registroMedicoRepository.save(registro);
    }
}
