package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.repositorio.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mascota findById(Integer id) {
        return mascotaRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Mascota> findByDueno(Dueno dueno) {
        return mascotaRepository.findByDueno(dueno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> findByEnAdopcion(boolean enAdopcion) {
        return mascotaRepository.findByEnAdopcion(enAdopcion);
    }

    @Override
    @Transactional
    public Mascota save(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        mascotaRepository.deleteById(id);
    }
}
