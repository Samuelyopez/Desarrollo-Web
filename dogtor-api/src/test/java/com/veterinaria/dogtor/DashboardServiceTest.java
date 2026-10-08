package com.veterinaria.dogtor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veterinaria.dogtor.dto.DashboardResponse;
import com.veterinaria.dogtor.dto.MedicamentoCantidad;
import com.veterinaria.dogtor.service.DashboardService;

// Con la BD en memoria los cargadores siembran el Excel y los 11 tratamientos de prueba
// (fechas relativas a hoy), así que los KPIs son siempre los mismos
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:dashboardtest")
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void calculaLosKpisConLosDatosDePrueba() {
        DashboardResponse d = dashboardService.obtener();

        // Últimos 30 días: tratamientos de hace 1, 2, 3, 5, 8, 12, 15 y 20 días
        assertEquals(8, d.tratamientosUltimoMes());
        assertEquals(10, d.unidadesUltimoMes());
        MedicamentoCantidad primero = d.tratamientosPorMedicamento().get(0);
        assertEquals("Amoxicilina 500 mg", primero.medicamento());
        assertEquals(3, primero.tratamientos());
        assertEquals(4, primero.unidades());

        assertEquals(4, d.veterinariosActivos());
        assertEquals(1, d.veterinariosInactivos());
        assertEquals(20, d.mascotasTotales());
        assertEquals(19, d.mascotasActivas());

        assertEquals(214000.0, d.ventasTotales(), 0.001);
        assertEquals(100000.0, d.gananciasTotales(), 0.001);

        List<String> top = d.topMedicamentos().stream().map(t -> t.medicamento()).toList();
        assertEquals(List.of("Amoxicilina 500 mg", "Meloxicam 1.5 mg/ml", "Suero Ringer Lactato 500 ml"), top);
    }
}
