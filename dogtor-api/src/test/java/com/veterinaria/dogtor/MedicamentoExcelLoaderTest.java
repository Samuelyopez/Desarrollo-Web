package com.veterinaria.dogtor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veterinaria.dogtor.config.MedicamentoExcelLoader;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.service.MedicamentoService;

// El Excel de src/main/resources/excel ya se cargó al levantar el contexto
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:exceltest")
class MedicamentoExcelLoaderTest {

    @Autowired
    private MedicamentoExcelLoader loader;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private MedicamentoService medicamentoService;

    @Test
    void cargaLasFilasDelExcel() {
        assertEquals(20, medicamentoRepository.count());
        assertTrue(medicamentoRepository.existsByNombreIgnoreCase("Amoxicilina 500 mg"));
    }

    @Test
    void volverACargarNoDuplica() {
        long antes = medicamentoRepository.count();

        loader.run();

        assertEquals(antes, medicamentoRepository.count());
    }

    @Test
    void elDesplegableSoloTraeMedicamentosConUnidades() {
        List<Medicamento> todos = medicamentoService.listar(false);
        List<Medicamento> disponibles = medicamentoService.listar(true);

        assertFalse(disponibles.isEmpty());
        assertTrue(disponibles.stream().allMatch(m -> m.getUnidadesDisponibles() > 0));
        assertTrue(todos.stream().anyMatch(m -> m.getUnidadesDisponibles() == 0), "el Excel trae medicamentos agotados");
        assertEquals(todos.stream().filter(m -> m.getUnidadesDisponibles() > 0).count(), disponibles.size());
    }
}
