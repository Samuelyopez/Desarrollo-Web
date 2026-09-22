package com.veterinaria.dogtor;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Producto;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.ProductoService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(DuenoService duenoService, MascotaService mascotaService,
                                       ProductoService productoService, UsuarioService usuarioService) {
        return args -> {
            if (usuarioService.findAll().isEmpty()) {
                usuarioService.save(Usuario.builder().nombre("Administrador DogTor").correo("administrador@dogtor.com").password("admin123").rol(RolUsuario.ADMIN).build());
                usuarioService.save(Usuario.builder().nombre("Dr. Andres Felipe").correo("veterinario@dogtor.com").password("vet123").rol(RolUsuario.VETERINARIO).build());
            }

            if (duenoService.findAll().isEmpty()) {
                
                // 1. Crear Dueño Administrador (DogTor) para los animales en adopción
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
                mascotaService.save(Mascota.builder().nombre("Max").raza("Golden Retriever").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d1).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Luna").raza("Gato Siamés").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Falta rabia").dueno(d1).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Rocky").raza("Bulldog").edad("5 años").fotoUrl("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Coco").raza("Poodle").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Bella").raza("Gato Persa").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Toby").raza("Beagle").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1537151608804-ea6f4bc1c9a2?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Simba").raza("Mestizo").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Milo").raza("Labrador").edad("6 años").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Kira").raza("Husky").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1605568420125-4eb84e554902?auto=format&fit=crop&w=300&q=80").vacunas("Falta refuerzo").dueno(d5).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Thor").raza("Pastor Alemán").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d5).enAdopcion(false).build());

                // 4. Crear 6 Mascotas en Adopción
                mascotaService.save(Mascota.builder().nombre("Oreo").raza("Gato Callejero").edad("6 meses").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Desparasitado").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Firulais").raza("Mestizo").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Blanquita").raza("Poodle Mix").edad("1 año").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Vacuna múltiple").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Felix").raza("Gato Naranja").edad("3 años").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Rufo").raza("Labrador Mix").edad("4 años").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Pelusa").raza("Angora").edad("2 años").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());

                // 5. Crear 6 Productos Medicinales (Farmacia)
                productoService.save(Producto.builder().nombre("Probiótico Digestivo Plus").descripcion("Mejora la flora intestinal de perros y gatos.").precio(45000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Vitaminas Caninas Complex").descripcion("Suplemento vitamínico diario para perros activos.").precio(35000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Desparasitante NexGard").descripcion("Protección mensual contra pulgas y garrapatas.").precio(65000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Gotas Óticas Limpiadoras").descripcion("Prevención y tratamiento de otitis leve.").precio(25000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Jarabe para Tos Perrera").descripcion("Alivio rápido de síntomas respiratorios.").precio(38000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Crema Cicatrizante").descripcion("Crema tópica para heridas superficiales.").precio(22000.0).categoria("MEDICINAL").fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());

                // 6. Crear 6 Productos Recreacionales (Accesorios)
                productoService.save(Producto.builder().nombre("Pelota de Goma Resistente").descripcion("Ideal para perros de mordida fuerte.").precio(15000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Rascador de Torre para Gatos").descripcion("3 niveles con ratón colgante.").precio(120000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Correa Retráctil 5 Metros").descripcion("Correa con freno automático y linterna.").precio(45000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1601758177266-bc599de87707?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Cama Acolchada Ortopédica").descripcion("Espuma con memoria para razas grandes.").precio(180000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1541781774459-bb2af280528e?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Comedero Interactivo Lento").descripcion("Previene problemas gástricos.").precio(32000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Ratón a Control Remoto").descripcion("Juguete interactivo para gatos curiosos.").precio(55000.0).categoria("RECREACIONAL").fotoUrl("https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=300&q=80").build());
            }
        };
    }
}
