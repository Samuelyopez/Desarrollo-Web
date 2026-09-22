package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Veterinario;
import com.veterinaria.dogtor.repositorio.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario findById(Integer id) {
        return veterinarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario findByCorreo(String correo) {
        return veterinarioRepository.findByUsuario_Correo(correo).orElse(null);
    }

    @Override
    @Transactional
    public Veterinario save(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        veterinarioRepository.deleteById(id);
    }
}
