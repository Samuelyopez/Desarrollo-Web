package com.veterinaria.dogtor.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.DashboardResponse;
import com.veterinaria.dogtor.dto.MedicamentoCantidad;
import com.veterinaria.dogtor.dto.TopMedicamento;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int DIAS_ULTIMO_MES = 30;
    private static final int TAMANO_TOP = 3;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse obtener() {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.minusDays(DIAS_ULTIMO_MES);

        // Los totales del mes salen de la misma tabla: así el KPI 1 siempre cuadra con el KPI 2
        List<MedicamentoCantidad> porMedicamento = tratamientoRepository.porMedicamentoEntre(desde, hoy);
        long tratamientosMes = porMedicamento.stream().mapToLong(MedicamentoCantidad::tratamientos).sum();
        long unidadesMes = porMedicamento.stream().mapToLong(MedicamentoCantidad::unidades).sum();

        List<TopMedicamento> top = tratamientoRepository.topMedicamentos(PageRequest.of(0, TAMANO_TOP));

        return new DashboardResponse(
                desde,
                hoy,
                tratamientosMes,
                unidadesMes,
                porMedicamento,
                veterinarioRepository.countByUsuario_Activo(true),
                veterinarioRepository.countByUsuario_Activo(false),
                mascotaRepository.count(),
                mascotaRepository.countByActivaTrue(),
                tratamientoRepository.ventasTotales(),
                tratamientoRepository.gananciasTotales(),
                top);
    }
}
