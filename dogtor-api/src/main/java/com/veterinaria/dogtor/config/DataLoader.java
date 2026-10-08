package com.veterinaria.dogtor.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.entities.Administrador;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.repository.AdministradorRepository;
import com.veterinaria.dogtor.repository.DuenoRepository;
import com.veterinaria.dogtor.repository.UsuarioRepository;
import com.veterinaria.dogtor.repository.VeterinarioRepository;

// Datos de prueba. Solo se cargan si la BD está vacía (la BD es un archivo y sobrevive a los reinicios).
// Para volver a sembrar: detener la API y borrar la carpeta data/
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("La base de datos ya tiene datos: no se cargan datos de prueba");
            return;
        }
        cargarAdministradores();
        cargarVeterinarios();
        cargarDuenosYMascotas();
        log.info("Datos de prueba cargados: {} administradores, {} veterinarios, {} dueños",
                administradorRepository.count(), veterinarioRepository.count(), duenoRepository.count());
    }

    private void cargarAdministradores() {
        administradorRepository.saveAll(List.of(
                new Administrador("900000001", "Administrador DogTor", new Usuario("admin@dogtor.com", "admin123", Rol.ADMIN)),
                new Administrador("900000002", "Gerente DogTor", new Usuario("gerente@dogtor.com", "admin123", Rol.ADMIN))));
    }

    private void cargarVeterinarios() {
        // Pedro está inactivo (vacaciones) para probar que no puede entrar al portal
        Usuario usuarioPedro = new Usuario("pedro.vet@dogtor.com", "vet123", Rol.VETERINARIO);
        usuarioPedro.setActivo(false);

        veterinarioRepository.saveAll(List.of(
                new Veterinario("800000001", "Andrés Felipe Mora", "Medicina general", null, new Usuario("veterinario@dogtor.com", "vet123", Rol.VETERINARIO)),
                new Veterinario("800000002", "Laura Jiménez", "Cirugía", null, new Usuario("laura.vet@dogtor.com", "vet123", Rol.VETERINARIO)),
                new Veterinario("800000003", "Camilo Rojas", "Dermatología", null, new Usuario("camilo.vet@dogtor.com", "vet123", Rol.VETERINARIO)),
                new Veterinario("800000004", "Paula Castro", "Cardiología", null, new Usuario("paula.vet@dogtor.com", "vet123", Rol.VETERINARIO)),
                new Veterinario("800000005", "Pedro Salazar", "Ortopedia", null, usuarioPedro)));
    }

    private void cargarDuenosYMascotas() {
        Dueno juan = dueno("1000000001", "Juan Pérez", "3001234567", "juan.perez@correo.com");
        juan.agregarMascota(new Mascota("Max", "Golden Retriever", 1, 8.5, "Parvovirus", "/img/mascotas/1-max.jpg", true));
        juan.agregarMascota(new Mascota("Luna", "Gato Siamés", 1, 3.2, "Infección urinaria", "/img/mascotas/2-luna.jpg", true));

        Dueno maria = dueno("1000000002", "María López", "3012345678", "maria.lopez@correo.com");
        maria.agregarMascota(new Mascota("Rocky", "Bulldog Francés", 1, 11.0, "Dificultad respiratoria", "/img/mascotas/3-rocky.jpg", true));
        maria.agregarMascota(new Mascota("Bella", "Gato Persa", 3, 4.1, "Otitis", "/img/mascotas/4-bella.jpg", true));

        Dueno carlos = dueno("1000000003", "Carlos Ruiz", "3023456789", "carlos.ruiz@correo.com");
        carlos.agregarMascota(new Mascota("Toby", "Poodle", 2, 6.3, "Dermatitis", "/img/mascotas/5-toby.jpg", true));
        carlos.agregarMascota(new Mascota("Oreo", "Gato Naranja Común", 7, 5.0, "Insuficiencia renal", "/img/mascotas/6-oreo.jpg", true));

        Dueno ana = dueno("1000000004", "Ana Gómez", "3034567890", "ana.gomez@correo.com");
        ana.agregarMascota(new Mascota("Simba", "Golden Retriever", 4, 30.2, "Displasia de cadera", "/img/mascotas/7-simba.jpg", true));
        ana.agregarMascota(new Mascota("Felix", "Gato Común Europeo", 1, 2.8, "Gastroenteritis", "/img/mascotas/8-felix.jpg", false));

        Dueno luis = dueno("1000000005", "Luis Díaz", "3045678901", "luis.diaz@correo.com");
        luis.agregarMascota(new Mascota("Milo", "Labrador Retriever", 1, 9.0, "Moquillo", "/img/mascotas/9-milo.jpg", true));
        luis.agregarMascota(new Mascota("Pelusa", "Gato Negro", 1, 3.5, "", "/img/mascotas/10-pelusa.jpg", true));

        Dueno sofia = dueno("1000000006", "Sofía Martínez", "3056789012", "sofia.martinez@correo.com");
        sofia.agregarMascota(new Mascota("Thor", "Husky Siberiano", 1, 18.4, "Fractura de pata", "/img/mascotas/11-thor.jpg", true));
        sofia.agregarMascota(new Mascota("Michi", "Gato Atigrado", 3, 4.6, "Conjuntivitis", "/img/mascotas/12-michi.jpg", true));

        Dueno andres = dueno("1000000007", "Andrés Rodríguez", "3067890123", "andres.rodriguez@correo.com");
        andres.agregarMascota(new Mascota("Buddy", "Pastor Alemán", 2, 28.0, "Leishmaniasis", "/img/mascotas/13-buddy.jpg", true));
        andres.agregarMascota(new Mascota("Salem", "Gato Naranja", 7, 5.4, "Diabetes", "/img/mascotas/14-salem.jpg", true));

        Dueno camila = dueno("1000000008", "Camila Torres", "3078901234", "camila.torres@correo.com");
        camila.agregarMascota(new Mascota("Duke", "Mestizo", 4, 15.7, "Parásitos intestinales", "/img/mascotas/15-duke.jpg", true));
        camila.agregarMascota(new Mascota("Whiskers", "Ragdoll", 1, 3.0, "Gripe felina", "/img/mascotas/16-whiskers.jpg", true));

        Dueno diego = dueno("1000000009", "Diego Ramírez", "3089012345", "diego.ramirez@correo.com");
        diego.agregarMascota(new Mascota("Rex", "Chihuahua", 1, 1.8, "Hipoglucemia", "/img/mascotas/17-rex.jpg", true));
        diego.agregarMascota(new Mascota("Nala", "Gato Mestizo", 1, 3.3, "Herida por mordedura", "/img/mascotas/18-nala.jpg", true));

        Dueno valentina = dueno("1000000010", "Valentina Jiménez", "3090123456", "valentina.jimenez@correo.com");
        valentina.agregarMascota(new Mascota("Zeus", "Pug", 1, 7.2, "Alergia alimentaria", "/img/mascotas/19-zeus.jpg", true));
        valentina.agregarMascota(new Mascota("Simona", "Abisinio", 3, 3.9, "Anemia", "/img/mascotas/20-simona.jpg", true));

        // La cascada guarda también el usuario y las mascotas de cada dueño
        duenoRepository.saveAll(List.of(juan, maria, carlos, ana, luis, sofia, andres, camila, diego, valentina));
    }

    private Dueno dueno(String cedula, String nombre, String celular, String correo) {
        return new Dueno(cedula, nombre, celular, new Usuario(correo, "cliente123", Rol.DUENO));
    }
}
