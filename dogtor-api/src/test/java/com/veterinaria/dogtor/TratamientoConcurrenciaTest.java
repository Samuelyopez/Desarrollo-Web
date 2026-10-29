package com.veterinaria.dogtor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;
import com.veterinaria.dogtor.service.TratamientoService;

// Dos veterinarios piden a la vez las últimas unidades: el bloqueo del medicamento
// (findByIdParaActualizar) hace que solo uno lo logre. Sin @Transactional en la prueba:
// cada hilo necesita su propia transacción real, por eso se limpia a mano al final
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:concurrenciatest")
class TratamientoConcurrenciaTest {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    private Medicamento medicamento;

    @Test
    void soloUnoSeLlevaLasUltimasUnidades() throws Exception {
        medicamento = medicamentoRepository.save(new Medicamento("Prueba concurrencia", 1000.0, 2000.0, 2, 0));
        Mascota max = mascotaRepository.findAllByOrderByNombreAsc().stream()
                .filter(m -> m.getNombre().equals("Max")).findFirst().orElseThrow();
        Long andres = veterinarioRepository.findByCedula("800000001").orElseThrow().getId();
        Long laura = veterinarioRepository.findByCedula("800000002").orElseThrow().getId();

        CountDownLatch salida = new CountDownLatch(1);
        ExecutorService hilos = Executors.newFixedThreadPool(2);
        List<Future<Boolean>> resultados = new ArrayList<>();
        for (Long vet : List.of(andres, laura)) {
            resultados.add(hilos.submit(() -> {
                salida.await();
                try {
                    tratamientoService.crear(new TratamientoRequest(max.getId(), vet, medicamento.getId(), 2));
                    return true;
                } catch (ConflictException e) {
                    return false;
                }
            }));
        }
        salida.countDown();

        int exitos = 0;
        for (Future<Boolean> r : resultados) {
            exitos += r.get() ? 1 : 0;
        }
        hilos.shutdown();

        assertEquals(1, exitos);
        Medicamento resultado = medicamentoRepository.findById(medicamento.getId()).orElseThrow();
        assertEquals(0, resultado.getUnidadesDisponibles());
        assertEquals(2, resultado.getUnidadesVendidas());
    }

    @AfterEach
    void limpiar() {
        if (medicamento != null) {
            tratamientoRepository.deleteAll(tratamientoRepository.findAll().stream()
                    .filter(t -> t.getMedicamento().getId().equals(medicamento.getId())).toList());
            medicamentoRepository.deleteById(medicamento.getId());
        }
    }
}
