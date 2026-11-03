package com.veterinaria.dogtor.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Usuario;

// CRUD del repositorio de dueños. @DataJpaTest crea una H2 de juguete, vacía y aparte de la real
// (no corre el DataLoader), y la deja como estaba después de cada prueba.
// flush() + clear() obligan a releer de la BD y no del caché de Hibernate
@DataJpaTest
class DuenoRepositoryTest {

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Dueno ana;

    // Se ejecuta antes de cada prueba: un dueño con una mascota
    @BeforeEach
    void setup() {
        ana = new Dueno("1010101010", "Ana Gómez", "3001112233", new Usuario("ana@correo.com", "123456", Rol.DUENO));
        ana.agregarMascota(new Mascota("Rocky", "Labrador", 4, 30.5, "Otitis", null, true));
        ana = duenoRepository.save(ana);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void DuenoRepository_Save_RetornaDueno() {
        // Arrange
        Dueno carlos = new Dueno("2020202020", "Carlos Ruiz", "3109998877",
                new Usuario("carlos@correo.com", "123456", Rol.DUENO));

        // Act
        Dueno guardado = duenoRepository.save(carlos);

        // Assert
        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getUsuario().getId()).isNotNull();
        assertThat(usuarioRepository.existsByCorreo("carlos@correo.com")).isTrue();
    }

    @Test
    void DuenoRepository_FindAll_RetornaListaNoVacia() {
        // Arrange
        duenoRepository.save(new Dueno("2020202020", "Carlos Ruiz", "3109998877",
                new Usuario("carlos@correo.com", "123456", Rol.DUENO)));

        // Act
        List<Dueno> duenos = duenoRepository.findAll();

        // Assert
        assertThat(duenos).isNotNull();
        assertThat(duenos.size()).isGreaterThan(0);
        assertThat(duenos.size()).isEqualTo(2);
    }

    @Test
    void DuenoRepository_FindById_RetornaDueno() {
        // Act
        Optional<Dueno> encontrado = duenoRepository.findById(ana.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Ana Gómez");
        assertThat(encontrado.get().getCorreo()).isEqualTo("ana@correo.com");
        assertThat(encontrado.get().getCantidadMascotas()).isEqualTo(1);
    }

    @Test
    void DuenoRepository_FindById_RetornaVacioSiNoExiste() {
        // Arrange
        Long idInexistente = -1L;

        // Act
        Optional<Dueno> encontrado = duenoRepository.findById(idInexistente);

        // Assert
        assertThat(encontrado).isEmpty();
    }

    @Test
    void DuenoRepository_Update_RetornaDuenoActualizado() {
        // Arrange
        Dueno dueno = duenoRepository.findById(ana.getId()).orElseThrow();
        dueno.setNombre("Ana María Gómez");
        dueno.setCelular("3205556677");

        // Act
        Dueno actualizado = duenoRepository.save(dueno);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(actualizado).isNotNull();
        Dueno enBd = duenoRepository.findById(ana.getId()).orElseThrow();
        assertThat(enBd.getNombre()).isEqualTo("Ana María Gómez");
        assertThat(enBd.getCelular()).isEqualTo("3205556677");
    }

    @Test
    void DuenoRepository_DeleteById_BorraDuenoUsuarioYMascotas() {
        // Arrange
        Long usuarioId = duenoRepository.findById(ana.getId()).orElseThrow().getUsuario().getId();

        // Act
        duenoRepository.deleteById(ana.getId());
        entityManager.flush();

        // Assert
        assertThat(duenoRepository.findById(ana.getId())).isEmpty();
        assertThat(usuarioRepository.findById(usuarioId)).isEmpty();
        assertThat(mascotaRepository.findByDueno_Id(ana.getId())).isEmpty();
    }
}
