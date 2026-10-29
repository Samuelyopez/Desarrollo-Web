package com.veterinaria.dogtor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;
import com.veterinaria.dogtor.service.TratamientoService;

// Cada prueba hace rollback (@Transactional): no cambia el stock que usan las demás pruebas.
// Los medicamentos se crean aquí con unidades conocidas para no depender de las filas del Excel
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tratamientotest")
@Transactional
class TratamientoServiceTest {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    private Mascota max;
    private Veterinario andres;

    @BeforeEach
    void datos() {
        max = mascota("Max");
        andres = veterinarioRepository.findByCedula("800000001").orElseThrow();
    }

    @Test
    void crearDescuentaDisponiblesYSumaVendidas() {
        Medicamento med = medicamento("Prueba con stock", 5, 1);

        Tratamiento t = tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 2));

        assertEquals(2, t.getCantidad());
        Medicamento actualizado = medicamentoRepository.findById(med.getId()).orElseThrow();
        assertEquals(3, actualizado.getUnidadesDisponibles());
        assertEquals(3, actualizado.getUnidadesVendidas());
        assertTrue(tratamientoService.historialDe(max.getId()).stream().anyMatch(h -> h.getId().equals(t.getId())));
    }

    @Test
    void noSeAsignaSiNoQuedanUnidades() {
        Medicamento med = medicamento("Prueba agotado", 0, 7);

        ConflictException e = assertThrows(ConflictException.class,
                () -> tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 1)));

        assertEquals("No quedan unidades de Prueba agotado", e.getMessage());
        assertEquals(0, medicamentoRepository.findById(med.getId()).orElseThrow().getUnidadesDisponibles());
    }

    @Test
    void noSeAsignaMasDeLoDisponible() {
        Medicamento med = medicamento("Prueba poco stock", 2, 0);

        ConflictException e = assertThrows(ConflictException.class,
                () -> tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 3)));

        assertEquals("Solo quedan 2 unidades de Prueba poco stock", e.getMessage());
        assertEquals(2, medicamentoRepository.findById(med.getId()).orElseThrow().getUnidadesDisponibles());
    }

    @Test
    void elVeterinarioInactivoNoPuedeDarTratamiento() {
        Medicamento med = medicamento("Prueba vet inactivo", 5, 0);
        Veterinario pedro = veterinarioRepository.findByCedula("800000005").orElseThrow();

        assertThrows(ConflictException.class,
                () -> tratamientoService.crear(request(max.getId(), pedro.getId(), med.getId(), 1)));
        assertEquals(5, medicamentoRepository.findById(med.getId()).orElseThrow().getUnidadesDisponibles());
    }

    @Test
    void laMascotaInactivaNoRecibeTratamiento() {
        Medicamento med = medicamento("Prueba mascota inactiva", 5, 0);
        Mascota felix = mascota("Felix");

        assertThrows(ConflictException.class,
                () -> tratamientoService.crear(request(felix.getId(), andres.getId(), med.getId(), 1)));
    }

    @Test
    void idsInexistentesDan404() {
        Medicamento med = medicamento("Prueba ids", 5, 0);

        assertThrows(NotFoundException.class,
                () -> tratamientoService.crear(request(-1L, andres.getId(), med.getId(), 1)));
        assertThrows(NotFoundException.class,
                () -> tratamientoService.crear(request(max.getId(), -1L, med.getId(), 1)));
        assertThrows(NotFoundException.class,
                () -> tratamientoService.crear(request(max.getId(), andres.getId(), -1L, 1)));
        assertThrows(NotFoundException.class, () -> tratamientoService.historialDe(-1L));
    }

    private Mascota mascota(String nombre) {
        return mascotaRepository.findAllByOrderByNombreAsc().stream()
                .filter(m -> m.getNombre().equals(nombre)).findFirst().orElseThrow();
    }

    private Medicamento medicamento(String nombre, int disponibles, int vendidas) {
        return medicamentoRepository.save(new Medicamento(nombre, 1000.0, 2000.0, disponibles, vendidas));
    }

    private TratamientoRequest request(Long mascotaId, Long veterinarioId, Long medicamentoId, int cantidad) {
        return new TratamientoRequest(mascotaId, veterinarioId, medicamentoId, cantidad);
    }
}
