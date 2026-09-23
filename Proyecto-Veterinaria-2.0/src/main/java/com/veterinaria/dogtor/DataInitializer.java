package com.veterinaria.dogtor;

import com.veterinaria.dogtor.entidad.Administrador;
import com.veterinaria.dogtor.entidad.Droga;
import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RegistroMedico;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.entidad.Veterinario;
import com.veterinaria.dogtor.servicio.AdministradorService;
import com.veterinaria.dogtor.servicio.DrogaService;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.RegistroMedicoService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import com.veterinaria.dogtor.servicio.VeterinarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    // {nombre, raza, idFotoUnsplash}
    private static final String[][] RAZAS_PERRO = {
            {"Golden Retriever", "1552053831-71594a27632d"},
            {"Bulldog Francés", "1583511655857-d19b40a7a54e"},
            {"Poodle", "1591768575198-88dac53fbd0a"},
            {"Golden Retriever", "1591160690555-5debfba289f0"},
            {"Labrador Retriever", "1611003228941-98852ba62227"},
            {"Husky Siberiano", "1568572933382-74d440642117"},
            {"Pastor Alemán", "1589952283406-b53a7d1347e8"},
            {"Mestizo", "1543466835-00a7907e9de1"},
            {"Chihuahua", "1548199973-03cce0bbc87b"},
            {"Pug", "1517423440428-a5a00ad493e8"},
            {"Mestizo", "1601758228041-f3b2795255f1"},
            {"Cavalier King Charles", "1560807707-8cc77767d783"},
            {"Border Collie", "1587300003388-59208cc962cb"},
            {"Samoyedo", "1596492784531-6e6eb5ea9993"},
            {"Husky Siberiano", "1590419690008-905895e8fe0d"},
            {"Boxer", "1591946614720-90a587da4a36"},
            {"Corgi", "1560743641-3914f2c45636"}
    };

    private static final String[][] RAZAS_GATO = {
            {"Gato Siamés", "1513360371669-4adf3dd7dff8"},
            {"Gato Persa", "1514888286974-6c03e2ca1dba"},
            {"Gato Naranja Común", "1573865526739-10659fec78a5"},
            {"Gato Común Europeo", "1543852786-1cf6624b9987"},
            {"Gato Negro", "1546527868-ccb7ee7dfa6a"},
            {"Gato Atigrado", "1495360010541-f48722b34f7d"},
            {"Gato Naranja", "1592194996308-7b43878e84a6"},
            {"Ragdoll", "1526336024174-e58f5cdd8e13"},
            {"Gato Mestizo", "1615789591457-74a63395c990"},
            {"Abisinio", "1571566882372-1598d88abd90"},
            {"Gato Callejero", "1618826411640-d6df44dd3f7a"}
    };

    private static final String[] NOMBRES_PERRO = {
            "Max", "Rocky", "Toby", "Simba", "Milo", "Thor", "Buddy", "Duke", "Rex", "Zeus",
            "Bruno", "Leo", "Oso", "Copito", "Canela", "Firulais", "Balto", "Lucky", "Coco", "Bimbo",
            "Otto", "Bolt", "Rambo", "Sombra", "Chispa", "Tango", "Diesel", "Maui", "Kobe", "Bruce",
            "Bandido", "Capitán", "Ringo", "Draco", "Simón", "Loki", "Hércules", "Apolo", "Sultán", "Chester",
            "Fido", "Pancho", "Chocolate", "Gordo", "Príncipe", "Rufo", "Nerón", "Toto", "Baxter", "Jack"
    };

    private static final String[] NOMBRES_GATO = {
            "Luna", "Bella", "Oreo", "Felix", "Pelusa", "Michi", "Salem", "Whiskers", "Nala", "Simona",
            "Tom", "Garfield", "Mimi", "Kitty", "Sasha", "Rayo", "Motita", "Sombrita", "Copo", "Cielo",
            "Perla", "Estrella", "Tigresa", "Coquito", "Mostaza", "Cenizo", "Blanquita", "Negrita", "Manchas", "Minino",
            "Pumba", "Bigotes", "Misha", "Nieve", "Olivia", "Momo", "Kira", "Cleo", "Duquesa", "Toffee",
            "Sushi", "Pixel", "Ronroncito", "Chispita", "Estrellita", "Dulce", "Miel", "Trufa", "Coral", "Luz"
    };

    private static final String[] EDADES = {
            "2 meses", "4 meses", "6 meses", "8 meses", "10 meses", "1 año",
            "1 año y medio", "2 años", "3 años", "4 años", "5 años", "7 años"
    };

    private static final String[] VACUNAS = {
            "Al día", "Falta refuerzo anual", "Falta vacuna de rabia",
            "Desparasitado", "Esquema completo", "Vacuna múltiple pendiente"
    };

    // {nombreCompleto, correo}
    private static final String[][] DUENOS = {
            {"Juan Pérez", "juan.perez@example.com"}, {"María López", "maria.lopez@example.com"},
            {"Carlos Ruiz", "carlos.ruiz@example.com"}, {"Ana Gómez", "ana.gomez@example.com"},
            {"Luis Díaz", "luis.diaz@example.com"}, {"Sofía Martínez", "sofia.martinez@example.com"},
            {"Andrés Rodríguez", "andres.rodriguez@example.com"}, {"Camila Torres", "camila.torres@example.com"},
            {"Diego Ramírez", "diego.ramirez@example.com"}, {"Valentina Jiménez", "valentina.jimenez@example.com"},
            {"Sergio Santos", "sergio.santos@example.com"}, {"Laura Ríos", "laura.rios@example.com"},
            {"Miguel Herrera", "miguel.herrera@example.com"}, {"Paula Castro", "paula.castro@example.com"},
            {"Felipe Ortiz", "felipe.ortiz@example.com"}, {"Daniela Vargas", "daniela.vargas@example.com"},
            {"Santiago Medina", "santiago.medina@example.com"}, {"Isabella Morales", "isabella.morales@example.com"},
            {"Alejandro Rojas", "alejandro.rojas@example.com"}, {"Mariana Reyes", "mariana.reyes@example.com"},
            {"Nicolás Guzmán", "nicolas.guzman@example.com"}, {"Gabriela Silva", "gabriela.silva@example.com"},
            {"David Cortés", "david.cortes@example.com"}, {"Valeria Peña", "valeria.pena@example.com"},
            {"Julián Mejía", "julian.mejia@example.com"}, {"Natalia Suárez", "natalia.suarez@example.com"},
            {"Óscar Vega", "oscar.vega@example.com"}, {"Carolina Acosta", "carolina.acosta@example.com"},
            {"Esteban Cárdenas", "esteban.cardenas@example.com"}, {"Juliana Camacho", "juliana.camacho@example.com"},
            {"Ricardo Salazar", "ricardo.salazar@example.com"}, {"Tatiana Molina", "tatiana.molina@example.com"},
            {"Fernando Bermúdez", "fernando.bermudez@example.com"}, {"Lorena Cuervo", "lorena.cuervo@example.com"},
            {"Mauricio Aguilar", "mauricio.aguilar@example.com"}, {"Adriana Navarro", "adriana.navarro@example.com"},
            {"Rodrigo Correa", "rodrigo.correa@example.com"}, {"Ximena Zapata", "ximena.zapata@example.com"},
            {"Iván Franco", "ivan.franco@example.com"}, {"Patricia Duarte", "patricia.duarte@example.com"},
            {"Jorge Restrepo", "jorge.restrepo@example.com"}, {"Catalina Pardo", "catalina.pardo@example.com"},
            {"Cristian Escobar", "cristian.escobar@example.com"}, {"Vanessa Arias", "vanessa.arias@example.com"},
            {"Rafael Cifuentes", "rafael.cifuentes@example.com"}, {"Melissa Montoya", "melissa.montoya@example.com"},
            {"Gustavo Quintero", "gustavo.quintero@example.com"}, {"Ángela Valencia", "angela.valencia@example.com"},
            {"Hernán Chávez", "hernan.chavez@example.com"}, {"Silvia Nieto", "silvia.nieto@example.com"}
    };

    @Bean
    public CommandLineRunner loadData(DuenoService duenoService, MascotaService mascotaService,
                                       UsuarioService usuarioService,
                                       RegistroMedicoService registroMedicoService,
                                       AdministradorService administradorService,
                                       VeterinarioService veterinarioService,
                                       DrogaService drogaService) {
        return args -> {
            Usuario veterinarioLogin = null;

            if (usuarioService.findAll().isEmpty()) {
                // 5 Administradores
                administradorService.save(Administrador.builder().cargo("Dirección General")
                        .usuario(Usuario.builder().nombre("Administrador DogTor").correo("administrador@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build()).build());
                administradorService.save(Administrador.builder().cargo("Recepción")
                        .usuario(Usuario.builder().nombre("Camila Torres").correo("recepcion@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build()).build());
                administradorService.save(Administrador.builder().cargo("Gerencia General")
                        .usuario(Usuario.builder().nombre("Diego Ramirez").correo("gerencia@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build()).build());
                administradorService.save(Administrador.builder().cargo("Finanzas")
                        .usuario(Usuario.builder().nombre("Paula Ortiz").correo("finanzas@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build()).build());
                administradorService.save(Administrador.builder().cargo("Sistemas")
                        .usuario(Usuario.builder().nombre("Sergio Vidal").correo("sistemas@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build()).build());

                // 10 Veterinarios
                Veterinario vet1 = veterinarioService.save(Veterinario.builder().especialidad("Medicina General").numeroLicencia("VET-001")
                        .usuario(Usuario.builder().nombre("Dr. Andres Felipe").correo("veterinario@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Cirugía").numeroLicencia("VET-002")
                        .usuario(Usuario.builder().nombre("Dra. Laura Jimenez").correo("laura.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Dermatología").numeroLicencia("VET-003")
                        .usuario(Usuario.builder().nombre("Dr. Miguel Santos").correo("miguel.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Odontología").numeroLicencia("VET-004")
                        .usuario(Usuario.builder().nombre("Dra. Camila Rios").correo("camila.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Cardiología").numeroLicencia("VET-005")
                        .usuario(Usuario.builder().nombre("Dr. Felipe Herrera").correo("felipe.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Oftalmología").numeroLicencia("VET-006")
                        .usuario(Usuario.builder().nombre("Dra. Natalia Cortes").correo("natalia.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Traumatología").numeroLicencia("VET-007")
                        .usuario(Usuario.builder().nombre("Dr. Ricardo Salcedo").correo("ricardo.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Medicina Interna").numeroLicencia("VET-008")
                        .usuario(Usuario.builder().nombre("Dra. Valentina Ospina").correo("valentina.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Nutrición Animal").numeroLicencia("VET-009")
                        .usuario(Usuario.builder().nombre("Dr. Esteban Moreno").correo("esteban.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());
                veterinarioService.save(Veterinario.builder().especialidad("Anestesiología").numeroLicencia("VET-010")
                        .usuario(Usuario.builder().nombre("Dra. Juliana Prada").correo("juliana.vet@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build()).build());

                veterinarioLogin = vet1.getUsuario();
            }

            // Se ejecuta siempre (no solo en BD vacía) para reparar bases de datos que ya
            // tenían usuarios ADMIN/VETERINARIO antes de que estas tablas propias existieran.
            administradorService.sincronizarConUsuarios();
            veterinarioService.sincronizarConUsuarios();

            if (drogaService.findAll().isEmpty()) {
                drogaService.save(Droga.builder().nombre("Amoxicilina").descripcion("Antibiótico de amplio espectro.").dosisRecomendada("10-20 mg/kg cada 12 horas").build());
                drogaService.save(Droga.builder().nombre("Meloxicam").descripcion("Antiinflamatorio no esteroideo.").dosisRecomendada("0.1 mg/kg cada 24 horas").build());
                drogaService.save(Droga.builder().nombre("Ivermectina").descripcion("Antiparasitario de amplio espectro.").dosisRecomendada("0.2 mg/kg dosis única").build());
                drogaService.save(Droga.builder().nombre("Prednisona").descripcion("Corticoide para procesos inflamatorios y alérgicos.").dosisRecomendada("0.5-1 mg/kg cada 24 horas").build());
                drogaService.save(Droga.builder().nombre("Dexametasona").descripcion("Corticoide de acción rápida.").dosisRecomendada("0.1-0.2 mg/kg según indicación").build());
            }

            if (duenoService.findAll().isEmpty()) {
                Mascota mascotaDemo = null;

                // 50 Dueños, cada uno con 2 mascotas (1 perro + 1 gato) => 100 mascotas
                for (int i = 0; i < DUENOS.length; i++) {
                    Dueno dueno = duenoService.save(Dueno.builder().nombre(DUENOS[i][0])
                            .usuario(Usuario.builder().correo(DUENOS[i][1]).password("1234").rol(RolUsuario.DUENO).build())
                            .build());

                    String[] razaPerro = RAZAS_PERRO[i % RAZAS_PERRO.length];
                    Mascota perro = mascotaService.save(Mascota.builder()
                            .nombre(NOMBRES_PERRO[i])
                            .raza(razaPerro[0])
                            .edad(EDADES[(i * 3) % EDADES.length])
                            .fotoUrl("https://images.unsplash.com/photo-" + razaPerro[1] + "?auto=format&fit=crop&w=300&q=80")
                            .vacunas(VACUNAS[i % VACUNAS.length])
                            .dueno(dueno).build());

                    String[] razaGato = RAZAS_GATO[i % RAZAS_GATO.length];
                    mascotaService.save(Mascota.builder()
                            .nombre(NOMBRES_GATO[i])
                            .raza(razaGato[0])
                            .edad(EDADES[(i * 3 + 5) % EDADES.length])
                            .fotoUrl("https://images.unsplash.com/photo-" + razaGato[1] + "?auto=format&fit=crop&w=300&q=80")
                            .vacunas(VACUNAS[(i + 2) % VACUNAS.length])
                            .dueno(dueno).build());

                    if (i == 0) {
                        mascotaDemo = perro;
                    }
                }

                if (veterinarioLogin != null && mascotaDemo != null) {
                    List<Droga> drogas = drogaService.findAll();
                    Droga meloxicam = drogas.stream().filter(d -> d.getNombre().equals("Meloxicam")).findFirst().orElse(null);
                    Droga amoxicilina = drogas.stream().filter(d -> d.getNombre().equals("Amoxicilina")).findFirst().orElse(null);

                    registroMedicoService.save(RegistroMedico.builder().mascota(mascotaDemo).veterinario(veterinarioLogin)
                            .diagnostico("Chequeo general de rutina, sin hallazgos relevantes.")
                            .tratamiento("Ninguno, próximo control en 6 meses.").build());
                    registroMedicoService.save(RegistroMedico.builder().mascota(mascotaDemo).veterinario(veterinarioLogin)
                            .diagnostico("Leve otitis en oído derecho.")
                            .tratamiento("Gotas óticas cada 12 horas por 7 días.")
                            .drogas(amoxicilina != null && meloxicam != null ? List.of(amoxicilina, meloxicam) : List.of())
                            .build());
                }
            }
        };
    }
}
