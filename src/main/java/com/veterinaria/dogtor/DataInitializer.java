package com.veterinaria.dogtor;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Producto;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(DuenoService duenoService, MascotaService mascotaService, ProductoService productoService) {
        return args -> {
            if (duenoService.findAll().isEmpty()) {
                
                // 1. Crear Dueño Administrador (DogTor) para los animales en adopción
                Dueno admin = Dueno.builder().nombre("Veterinaria DogTor").correo("admin@dogtor.com").password("admin123").admin(true).build();
                admin = duenoService.save(admin);

                // 2. Crear 5 Dueños regulares
                Dueno d1 = duenoService.save(Dueno.builder().nombre("Juan Perez").correo("juan@example.com").password("1234").admin(false).build());
                Dueno d2 = duenoService.save(Dueno.builder().nombre("Maria Lopez").correo("maria@example.com").password("1234").admin(false).build());
                Dueno d3 = duenoService.save(Dueno.builder().nombre("Carlos Ruiz").correo("carlos@example.com").password("1234").admin(false).build());
                Dueno d4 = duenoService.save(Dueno.builder().nombre("Ana Gomez").correo("ana@example.com").password("1234").admin(false).build());
                Dueno d5 = duenoService.save(Dueno.builder().nombre("Luis Diaz").correo("luis@example.com").password("1234").admin(false).build());

                // 3. Crear 10 Mascotas (2 por cada dueño regular)
                mascotaService.save(Mascota.builder().nombre("Max").especie("Perro").raza("Golden Retriever").edad("3 años").peso(30.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d1).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Luna").especie("Gato").raza("Siamés").edad("2 años").peso(4.5).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Falta rabia").dueno(d1).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Rocky").especie("Perro").raza("Bulldog").edad("5 años").peso(25.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Coco").especie("Perro").raza("Poodle").edad("1 año").peso(8.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d2).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Bella").especie("Gato").raza("Persa").edad("4 años").peso(5.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Toby").especie("Perro").raza("Beagle").edad("2 años").peso(15.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1537151608804-ea6f4bc1c9a2?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d3).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Simba").especie("Perro").raza("Mestizo").edad("1 año").peso(12.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Milo").especie("Perro").raza("Labrador").edad("6 años").peso(32.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d4).enAdopcion(false).build());
                
                mascotaService.save(Mascota.builder().nombre("Kira").especie("Perro").raza("Husky").edad("3 años").peso(28.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1605568420125-4eb84e554902?auto=format&fit=crop&w=300&q=80").vacunas("Falta refuerzo").dueno(d5).enAdopcion(false).build());
                mascotaService.save(Mascota.builder().nombre("Thor").especie("Perro").raza("Pastor Alemán").edad("4 años").peso(35.0).observaciones("Control de rutina").fotoUrl("https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(d5).enAdopcion(false).build());

                // 4. Crear 6 Mascotas en Adopción
                mascotaService.save(Mascota.builder().nombre("Oreo").especie("Gato").raza("Callejero").edad("6 meses").peso(3.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").vacunas("Desparasitado").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Firulais").especie("Perro").raza("Mestizo").edad("2 años").peso(18.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Blanquita").especie("Perro").raza("Poodle Mix").edad("1 año").peso(7.5).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").vacunas("Vacuna múltiple").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Felix").especie("Gato").raza("Naranja").edad("3 años").peso(4.8).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Rufo").especie("Perro").raza("Labrador Mix").edad("4 años").peso(26.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());
                mascotaService.save(Mascota.builder().nombre("Pelusa").especie("Gato").raza("Angora").edad("2 años").peso(4.2).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").vacunas("Al día").dueno(admin).enAdopcion(true).build());

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
