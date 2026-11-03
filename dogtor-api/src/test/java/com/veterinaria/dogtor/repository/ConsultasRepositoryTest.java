package com.veterinaria.dogtor.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.veterinaria.dogtor.dto.MedicamentoCantidad;
import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;

// Pruebas de las consultas escritas a mano con @Query (JPQL) en los repositorios.
// Ambiente controlado (nada aleatorio), creado antes de cada prueba:
//   Ana (1010101010): Rocky (Labrador) y Luna (Siamés) | Carlos (2020202020): Toby (Beagle)
//   Laura (Cirugía, activa) y Pedro (Dermatología, inactivo)
//   Tratamientos: Rocky hace 1 y 5 días y Toby hace 10 días (Laura); Luna hace 40 días (Pedro)
@DataJpaTest
class ConsultasRepositoryTest {

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    private final LocalDate hoy = LocalDate.now();

    private Mascota rocky;
    private Mascota toby;
    private Veterinario laura;

    @BeforeEach
    void setup() {
        Dueno ana = new Dueno("1010101010", "Ana Gómez", "3001112233", new Usuario("ana@correo.com", "123456", Rol.DUENO));
        rocky = new Mascota("Rocky", "Labrador", 4, 30.5, "Otitis", null, true);
        Mascota luna = new Mascota("Luna", "Siamés", 2, 4.2, null, null, true);
        ana.agregarMascota(rocky);
        ana.agregarMascota(luna);
        duenoRepository.save(ana);

        Dueno carlos = new Dueno("2020202020", "Carlos Ruiz", "3109998877",
                new Usuario("carlos@correo.com", "123456", Rol.DUENO));
        toby = new Mascota("Toby", "Beagle", 6, 12.0, "Gastritis", null, true);
        carlos.agregarMascota(toby);
        duenoRepository.save(carlos);

        laura = veterinarioRepository.save(new Veterinario("800000101", "Laura Méndez", "Cirugía", null,
                new Usuario("laura@dogtor.com", "123456", Rol.VETERINARIO)));
        Usuario usuarioPedro = new Usuario("pedro@dogtor.com", "123456", Rol.VETERINARIO);
        usuarioPedro.setActivo(false);
        Veterinario pedro = veterinarioRepository.save(
                new Veterinario("800000102", "Pedro Díaz", "Dermatología", null, usuarioPedro));

        Medicamento amoxicilina = medicamentoRepository.save(new Medicamento("Amoxicilina", 1000.0, 2500.0, 50, 0));
        Medicamento meloxicam = medicamentoRepository.save(new Medicamento("Meloxicam", 2000.0, 4000.0, 50, 0));

        tratamientoRepository.save(new Tratamiento(hoy.minusDays(1), 2, rocky, laura, amoxicilina));
        tratamientoRepository.save(new Tratamiento(hoy.minusDays(5), 1, rocky, laura, meloxicam));
        tratamientoRepository.save(new Tratamiento(hoy.minusDays(10), 1, toby, laura, amoxicilina));
        tratamientoRepository.save(new Tratamiento(hoy.minusDays(40), 3, luna, pedro, meloxicam));
    }

    // DuenoRepository.buscar: nombre, celular o correo, sin distinguir mayúsculas
    @Test
    void DuenoRepository_Buscar_RetornaDuenosQueCoinciden() {
        // Act
        List<Dueno> porNombre = duenoRepository.buscar("ANA");
        List<Dueno> porCelular = duenoRepository.buscar("310999");
        List<Dueno> porCorreo = duenoRepository.buscar("carlos@");

        // Assert
        assertThat(porNombre).extracting(Dueno::getNombre).containsExactly("Ana Gómez");
        assertThat(porCelular).extracting(Dueno::getNombre).containsExactly("Carlos Ruiz");
        assertThat(porCorreo).extracting(Dueno::getNombre).containsExactly("Carlos Ruiz");
    }

    // MascotaRepository.buscar: por raza o por la cédula del dueño, ordenadas por nombre
    @Test
    void MascotaRepository_Buscar_RetornaMascotasPorRazaOCedulaDelDueno() {
        // Act
        List<Mascota> porRaza = mascotaRepository.buscar("beagle");
        List<Mascota> porCedula = mascotaRepository.buscar("1010101010");

        // Assert
        assertThat(porRaza).extracting(Mascota::getNombre).containsExactly("Toby");
        assertThat(porCedula).extracting(Mascota::getNombre).containsExactly("Luna", "Rocky");
    }

    // VeterinarioRepository.buscar: filtro por estado (activo) y por especialidad
    @Test
    void VeterinarioRepository_Buscar_RetornaVeterinariosPorEstadoYEspecialidad() {
        // Act
        List<Veterinario> activos = veterinarioRepository.buscar("", true);
        List<Veterinario> inactivos = veterinarioRepository.buscar("", false);
        List<Veterinario> dermatologos = veterinarioRepository.buscar("derma", null);

        // Assert
        assertThat(activos).extracting(Veterinario::getNombre).containsExactly("Laura Méndez");
        assertThat(inactivos).extracting(Veterinario::getNombre).containsExactly("Pedro Díaz");
        assertThat(dermatologos).extracting(Veterinario::getNombre).containsExactly("Pedro Díaz");
    }

    // TratamientoRepository.pacientesDe: agrupa por mascota, cuenta tratamientos y ordena por el más reciente
    @Test
    void TratamientoRepository_PacientesDe_RetornaMascotasAgrupadas() {
        // Act
        List<PacienteResponse> pacientes = tratamientoRepository.pacientesDe(laura.getId());

        // Assert
        assertThat(pacientes).hasSize(2);
        PacienteResponse primero = pacientes.get(0);
        assertThat(primero.mascotaId()).isEqualTo(rocky.getId());
        assertThat(primero.cantidadTratamientos()).isEqualTo(2);
        assertThat(primero.ultimoTratamiento()).isEqualTo(hoy.minusDays(1));
        assertThat(primero.duenoNombre()).isEqualTo("Ana Gómez");
        PacienteResponse segundo = pacientes.get(1);
        assertThat(segundo.mascotaId()).isEqualTo(toby.getId());
        assertThat(segundo.cantidadTratamientos()).isEqualTo(1);
    }

    // TratamientoRepository.porMedicamentoEntre: solo los últimos 30 días (deja fuera el de hace 40)
    @Test
    void TratamientoRepository_PorMedicamentoEntre_RetornaConteoDelPeriodo() {
        // Act
        List<MedicamentoCantidad> filas = tratamientoRepository.porMedicamentoEntre(hoy.minusDays(30), hoy);

        // Assert
        assertThat(filas).containsExactly(
                new MedicamentoCantidad("Amoxicilina", 2L, 3L),
                new MedicamentoCantidad("Meloxicam", 1L, 1L));
    }
}
