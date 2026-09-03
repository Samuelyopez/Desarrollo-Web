package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.repositorio.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota findById(Integer id) {
        return mascotaRepository.findById(id);
    }

    @Override
    public Mascota save(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public void delete(Integer id) {
        mascotaRepository.delete(id);
    }
}
