package com.veterinaria.dogtor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.DuenoRepository;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

// Pruebas de integración (sin mocks): @SpringBootTest levanta la aplicación completa y el servicio
// usa los repositorios reales. @ActiveProfiles("test") la conecta a la BD testdb en memoria
// (perfil test de application.properties), donde no corren DataLoader ni el Excel: los datos
// se crean en setup(). @Transactional deshace los cambios de cada prueba
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TratamientoServiceIntegrationTest {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    private Mascota max;
    private Mascota felix;
    private Veterinario andres;
    private Veterinario pedro;

    @BeforeEach
    void setup() {
        Dueno dueno = new Dueno("1010101010", "Ana Gómez", "3001112233", new Usuario("ana@correo.com", "123456", Rol.DUENO));
        max = new Mascota("Max", "Labrador", 5, 28.0, "Otitis", null, true);
        felix = new Mascota("Felix", "Criollo", 3, 4.0, null, null, false);
        dueno.agregarMascota(max);
        dueno.agregarMascota(felix);
        duenoRepository.save(dueno);

        andres = veterinarioRepository.save(new Veterinario("800000001", "Andrés Torres", "Medicina general", null,
                new Usuario("andres@dogtor.com", "123456", Rol.VETERINARIO)));
        Usuario usuarioPedro = new Usuario("pedro@dogtor.com", "123456", Rol.VETERINARIO);
        usuarioPedro.setActivo(false);
        pedro = veterinarioRepository.save(new Veterinario("800000005", "Pedro Díaz", "Dermatología", null, usuarioPedro));
    }

    @Test
    void TratamientoService_Crear_RetornaTratamientoYDescuentaUnidades() {
        // Arrange
        Medicamento med = medicamento("Prueba con stock", 5, 1);

        // Act
        Tratamiento t = tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 2));

        // Assert
        assertThat(t).isNotNull();
        assertThat(t.getId()).isNotNull();
        assertThat(t.getCantidad()).isEqualTo(2);
        Medicamento actualizado = medicamentoRepository.findById(med.getId()).orElseThrow();
        assertThat(actualizado.getUnidadesDisponibles()).isEqualTo(3);
        assertThat(actualizado.getUnidadesVendidas()).isEqualTo(3);
    }

    @Test
    void TratamientoService_HistorialDe_RetornaTratamientosDeLaMascota() {
        // Arrange
        Medicamento med = medicamento("Prueba historial", 5, 0);
        Tratamiento t = tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 1));

        // Act
        List<Tratamiento> historial = tratamientoService.historialDe(max.getId());

        // Assert
        assertThat(historial).extracting(Tratamiento::getId).containsExactly(t.getId());
    }

    @Test
    void TratamientoService_Crear_LanzaConflictSiNoHayUnidadesSuficientes() {
        // Arrange
        Medicamento med = medicamento("Prueba poco stock", 2, 0);

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(request(max.getId(), andres.getId(), med.getId(), 3)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Solo quedan 2 unidades de Prueba poco stock");
        assertThat(medicamentoRepository.findById(med.getId()).orElseThrow().getUnidadesDisponibles()).isEqualTo(2);
    }

    @Test
    void TratamientoService_Crear_LanzaConflictSiElVeterinarioEstaInactivo() {
        // Arrange
        Medicamento med = medicamento("Prueba vet inactivo", 5, 0);

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(request(max.getId(), pedro.getId(), med.getId(), 1)))
                .isInstanceOf(ConflictException.class);
        assertThat(medicamentoRepository.findById(med.getId()).orElseThrow().getUnidadesDisponibles()).isEqualTo(5);
    }

    @Test
    void TratamientoService_Crear_LanzaConflictSiLaMascotaEstaInactiva() {
        // Arrange
        Medicamento med = medicamento("Prueba mascota inactiva", 5, 0);

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(request(felix.getId(), andres.getId(), med.getId(), 1)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void TratamientoService_Crear_LanzaNotFoundConIdsInexistentes() {
        // Arrange
        Medicamento med = medicamento("Prueba ids", 5, 0);

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(request(-1L, andres.getId(), med.getId(), 1)))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> tratamientoService.crear(request(max.getId(), -1L, med.getId(), 1)))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> tratamientoService.crear(request(max.getId(), andres.getId(), -1L, 1)))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> tratamientoService.historialDe(-1L))
                .isInstanceOf(NotFoundException.class);
    }

    private Medicamento medicamento(String nombre, int disponibles, int vendidas) {
        return medicamentoRepository.save(new Medicamento(nombre, 1000.0, 2000.0, disponibles, vendidas));
    }

    private TratamientoRequest request(Long mascotaId, Long veterinarioId, Long medicamentoId, int cantidad) {
        return new TratamientoRequest(mascotaId, veterinarioId, medicamentoId, cantidad);
    }
}
