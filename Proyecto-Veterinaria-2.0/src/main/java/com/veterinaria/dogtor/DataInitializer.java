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

                // 5 Veterinarios
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

                // 1. Crear Dueño Administrador (DogTor)
                Dueno admin = Dueno.builder().nombre("Veterinaria DogTor")
                        .usuario(Usuario.builder().correo("admin@dogtor.com").password("admin123").rol(RolUsuario.DUENO).build())
                        .build();
                admin = duenoService.save(admin);

                // 2. Crear 5 Dueños regulares
                Dueno d1 = duenoService.save(Dueno.builder().nombre("Juan Perez")
                        .usuario(Usuario.builder().correo("juan@example.com").password("1234").rol(RolUsuario.DUENO).build()).build());
                Dueno d2 = duenoService.save(Dueno.builder().nombre("Maria Lopez")
                        .usuario(Usuario.builder().correo("maria@example.com").password("1234").rol(RolUsuario.DUENO).build()).build());
                Dueno d3 = duenoService.save(Dueno.builder().nombre("Carlos Ruiz")
                        .usuario(Usuario.builder().correo("carlos@example.com").password("1234").rol(RolUsuario.DUENO).build()).build());
                Dueno d4 = duenoService.save(Dueno.builder().nombre("Ana Gomez")
                        .usuario(Usuario.builder().correo("ana@example.com").password("1234").rol(RolUsuario.DUENO).build()).build());
                Dueno d5 = duenoService.save(Dueno.builder().nombre("Luis Diaz")
                        .usuario(Usuario.builder().correo("luis@example.com").password("1234").rol(RolUsuario.DUENO).build()).build());

                // 3. Crear 10 Mascotas (2 por cada dueño regular)
                Mascota max = mascotaService.save(Mascota.builder().nombre("Max").raza("Golden Retriever").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d1).build());
                mascotaService.save(Mascota.builder().nombre("Luna").raza("Gato Siamés").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Falta rabia").dueno(d1).activa(false).build());

                mascotaService.save(Mascota.builder().nombre("Rocky").raza("Bulldog").edad("5 años").fotoUrl("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).build());
                mascotaService.save(Mascota.builder().nombre("Coco").raza("Poodle").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).build());

                mascotaService.save(Mascota.builder().nombre("Bella").raza("Gato Persa").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).build());
                mascotaService.save(Mascota.builder().nombre("Toby").raza("Beagle").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1537151608804-ea6f4bc1c9a2?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).build());

                mascotaService.save(Mascota.builder().nombre("Simba").raza("Mestizo").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).build());
                mascotaService.save(Mascota.builder().nombre("Milo").raza("Labrador").edad("6 años").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).build());

                mascotaService.save(Mascota.builder().nombre("Kira").raza("Husky").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1605568420125-4eb84e554902?auto=format&fit=crop&w=300&q=80").vacunas("Falta refuerzo").dueno(d5).build());
                mascotaService.save(Mascota.builder().nombre("Thor").raza("Pastor Alemán").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d5).build());

                // 4. Seis mascotas adicionales de la clínica
                mascotaService.save(Mascota.builder().nombre("Oreo").raza("Gato Callejero").edad("6 meses").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Desparasitado").dueno(admin).build());
                mascotaService.save(Mascota.builder().nombre("Firulais").raza("Mestizo").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).build());
                mascotaService.save(Mascota.builder().nombre("Blanquita").raza("Poodle Mix").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Vacuna múltiple").dueno(admin).build());
                mascotaService.save(Mascota.builder().nombre("Felix").raza("Gato Naranja").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).build());
                mascotaService.save(Mascota.builder().nombre("Rufo").raza("Labrador Mix").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).build());
                mascotaService.save(Mascota.builder().nombre("Pelusa").raza("Angora").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).build());

                if (veterinarioLogin != null) {
                    List<Droga> drogas = drogaService.findAll();
                    Droga meloxicam = drogas.stream().filter(d -> d.getNombre().equals("Meloxicam")).findFirst().orElse(null);
                    Droga amoxicilina = drogas.stream().filter(d -> d.getNombre().equals("Amoxicilina")).findFirst().orElse(null);

                    registroMedicoService.save(RegistroMedico.builder().mascota(max).veterinario(veterinarioLogin)
                            .diagnostico("Chequeo general de rutina, sin hallazgos relevantes.")
                            .tratamiento("Ninguno, próximo control en 6 meses.").build());
                    registroMedicoService.save(RegistroMedico.builder().mascota(max).veterinario(veterinarioLogin)
                            .diagnostico("Leve otitis en oído derecho.")
                            .tratamiento("Gotas óticas cada 12 horas por 7 días.")
                            .drogas(amoxicilina != null && meloxicam != null ? List.of(amoxicilina, meloxicam) : List.of())
                            .build());
                }
            }
        };
    }
}
