package com.veterinaria.dogtor;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Producto;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.repositorio.UsuarioRepository;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(DuenoService duenoService, 
                                     MascotaService mascotaService, 
                                     ProductoService productoService,
                                     UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.findAll().isEmpty()) {
                
                // 1. Guardar Usuarios en la BD según sus roles (ADMIN, DUENO, VETERINARIO)
                Usuario uAdmin = usuarioRepository.save(Usuario.builder()
                        .correo("admin@dogtor.com")
                        .password("admin123")
                        .rol(com.veterinaria.dogtor.entidad.RolUsuario.ADMIN)
                        .build());

                Dueno admin = duenoService.save(Dueno.builder()
                        .nombre("Administrador Dogtor")
                        .telefono("3000000000")
                        .direccion("Clínica Veterinaria Central")
                        .usuario(uAdmin)
                        .build());

                // 2. Crear 50 clientes y 100 perros
                java.util.Random random = new java.util.Random();
                
                for (int i = 1; i <= 50; i++) {
                    Usuario u = usuarioRepository.save(Usuario.builder()
                            .correo("cliente" + i + "@example.com")
                            .password("1234")
                            .rol(com.veterinaria.dogtor.entidad.RolUsuario.DUENO)
                            .build());

                    Dueno d = duenoService.save(Dueno.builder()
                            .nombre("Cliente " + i)
                            .telefono("3001000" + String.format("%03d", i))
                            .direccion("Calle " + i + " # " + i + "-" + i)
                            .usuario(u)
                            .build());

                    for (int j = 1; j <= 2; j++) {
                        int mascotaId = (i - 1) * 2 + j;
                        mascotaService.save(Mascota.builder()
                                .nombre("Perro " + mascotaId)
                                .especie("Perro")
                                .raza("Raza " + random.nextInt(1, 10))
                                .edad(random.nextInt(1, 15))
                                .peso(10.0 + random.nextInt(0, 20))
                                .observaciones("Control de rutina")
                                .fotoUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80")

                                .dueno(d)
                                .enAdopcion(false)
                                .activo(true)
                                .build());
                    }
                }

                // 5. Crear 6 Mascotas en Adopción
                mascotaService.save(Mascota.builder().nombre("Oreo").especie("Gato").raza("Callejero").edad(0).peso(3.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());
                mascotaService.save(Mascota.builder().nombre("Firulais").especie("Perro").raza("Mestizo").edad(2).peso(18.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());
                mascotaService.save(Mascota.builder().nombre("Blanquita").especie("Perro").raza("Poodle Mix").edad(1).peso(7.5).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());
                mascotaService.save(Mascota.builder().nombre("Felix").especie("Gato").raza("Naranja").edad(3).peso(4.8).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());
                mascotaService.save(Mascota.builder().nombre("Rufo").especie("Perro").raza("Labrador Mix").edad(4).peso(26.0).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1591324535489-cecc7c5a0833?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());
                mascotaService.save(Mascota.builder().nombre("Pelusa").especie("Gato").raza("Angora").edad(2).peso(4.2).observaciones("Listo para adopción").fotoUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80").dueno(admin).enAdopcion(true).activo(true).build());

                // 6. Crear 6 Productos Medicinales (Farmacia)
                productoService.save(Producto.builder().nombre("Probiótico Digestivo Plus").descripcion("Mejora la flora intestinal de perros y gatos.").precio(45000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Vitaminas Caninas Complex").descripcion("Suplemento vitamínico diario para perros activos.").precio(35000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Desparasitante NexGard").descripcion("Protección mensual contra pulgas y garrapatas.").precio(65000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Gotas Óticas Limpiadoras").descripcion("Prevención y tratamiento de otitis leve.").precio(25000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Jarabe para Tos Perrera").descripcion("Alivio rápido de síntomas respiratorios.").precio(38000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Crema Cicatrizante").descripcion("Crema tópica para heridas superficiales.").precio(22000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL).fotoUrl("https://images.unsplash.com/photo-1628771065518-0d82f1938462?auto=format&fit=crop&w=300&q=80").build());

                // 7. Crear 6 Productos Recreacionales (Accesorios)
                productoService.save(Producto.builder().nombre("Pelota de Goma Resistente").descripcion("Ideal para perros de mordida fuerte.").precio(15000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Rascador de Torre para Gatos").descripcion("3 niveles con ratón colgante.").precio(120000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Correa Retráctil 5 Metros").descripcion("Correa con freno automático y linterna.").precio(45000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1601758177266-bc599de87707?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Cama Acolchada Ortopédica").descripcion("Espuma con memoria para razas grandes.").precio(180000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1541781774459-bb2af280528e?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Comedero Interactivo Lento").descripcion("Previene problemas gástricos.").precio(32000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=300&q=80").build());
                productoService.save(Producto.builder().nombre("Ratón a Control Remoto").descripcion("Juguete interactivo para gatos curiosos.").precio(55000.0).categoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL).fotoUrl("https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=300&q=80").build());
            }
        };
    }
}
