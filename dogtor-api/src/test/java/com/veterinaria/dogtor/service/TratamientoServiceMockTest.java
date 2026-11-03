package com.veterinaria.dogtor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.errors.ConflictException;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.repository.MascotaRepository;
import com.veterinaria.dogtor.repository.MedicamentoRepository;
import com.veterinaria.dogtor.repository.TratamientoRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

// Pruebas unitarias del servicio de tratamientos con Mockito: sin Spring ni BD, solo la lógica de negocio.
// La clase que se prueba va con @InjectMocks (la implementación, no la interfaz) y los repositorios
// que usa con @Mock. Cada llamada al repositorio dentro del servicio se simula con un when(...).thenReturn(...)
@ExtendWith(MockitoExtension.class)
class TratamientoServiceMockTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    private Mascota max;
    private Veterinario andres;
    private Medicamento amoxicilina;

    // Objetos "quemados" que devuelven los mocks
    @BeforeEach
    void setup() {
        max = new Mascota("Max", "Labrador", 5, 28.0, "Otitis", null, true);
        max.setId(1L);
        andres = new Veterinario("800000001", "Andrés Torres", "Medicina general", null,
                new Usuario("andres@dogtor.com", "123456", Rol.VETERINARIO));
        andres.setId(2L);
        amoxicilina = new Medicamento("Amoxicilina", 1000.0, 2500.0, 5, 1);
        amoxicilina.setId(3L);
    }

    @Test
    void TratamientoService_Crear_RetornaTratamientoYDescuentaUnidades() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(max));
        when(veterinarioRepository.findById(2L)).thenReturn(Optional.of(andres));
        when(medicamentoRepository.findByIdParaActualizar(3L)).thenReturn(Optional.of(amoxicilina));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Tratamiento t = tratamientoService.crear(new TratamientoRequest(1L, 2L, 3L, 2));

        // Assert
        assertThat(t).isNotNull();
        assertThat(t.getFecha()).isEqualTo(LocalDate.now());
        assertThat(t.getCantidad()).isEqualTo(2);
        assertThat(t.getMascotaNombre()).isEqualTo("Max");
        assertThat(t.getVeterinarioNombre()).isEqualTo("Andrés Torres");
        assertThat(t.getPrecioVenta()).isEqualTo(2500.0);
        assertThat(amoxicilina.getUnidadesDisponibles()).isEqualTo(3);
        assertThat(amoxicilina.getUnidadesVendidas()).isEqualTo(3);
        verify(tratamientoRepository).save(t);
    }

    // Regla de negocio: no se venden más unidades de las disponibles
    @Test
    void TratamientoService_Crear_LanzaConflictSinUnidadesSuficientes() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(max));
        when(veterinarioRepository.findById(2L)).thenReturn(Optional.of(andres));
        when(medicamentoRepository.findByIdParaActualizar(3L)).thenReturn(Optional.of(amoxicilina));

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(new TratamientoRequest(1L, 2L, 3L, 6)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Solo quedan 5 unidades de Amoxicilina");
        assertThat(amoxicilina.getUnidadesDisponibles()).isEqualTo(5);
        verify(tratamientoRepository, never()).save(any());
    }

    // Regla de negocio: solo las mascotas activas (en la clínica) reciben tratamiento
    @Test
    void TratamientoService_Crear_LanzaConflictSiLaMascotaEstaInactiva() {
        // Arrange
        max.setActiva(false);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(max));

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.crear(new TratamientoRequest(1L, 2L, 3L, 1)))
                .isInstanceOf(ConflictException.class);
        verify(veterinarioRepository, never()).findById(anyLong());
        verify(medicamentoRepository, never()).findByIdParaActualizar(anyLong());
        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    void TratamientoService_HistorialDe_RetornaListaDelRepositorio() {
        // Arrange
        List<Tratamiento> quemados = List.of(
                new Tratamiento(LocalDate.now(), 1, max, andres, amoxicilina),
                new Tratamiento(LocalDate.now().minusDays(3), 2, max, andres, amoxicilina));
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        when(tratamientoRepository.findByMascota_IdOrderByFechaDescIdDesc(1L)).thenReturn(quemados);

        // Act
        List<Tratamiento> historial = tratamientoService.historialDe(1L);

        // Assert
        assertThat(historial).isNotNull();
        assertThat(historial.size()).isEqualTo(2);
    }

    @Test
    void TratamientoService_HistorialDe_LanzaNotFoundSiLaMascotaNoExiste() {
        // Arrange
        when(mascotaRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> tratamientoService.historialDe(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No existe una mascota con el id 99");
        verify(tratamientoRepository, never()).findByMascota_IdOrderByFechaDescIdDesc(anyLong());
    }
}
