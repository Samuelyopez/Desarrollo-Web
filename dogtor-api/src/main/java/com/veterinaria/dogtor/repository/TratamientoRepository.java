package com.veterinaria.dogtor.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.dto.MedicamentoCantidad;
import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.dto.TopMedicamento;
import com.veterinaria.dogtor.entities.Tratamiento;

// Los finders que se envían al front traen medicamento y veterinario en el mismo SELECT
// (@EntityGraph): el JSON usa medicamentoNombre y veterinarioNombre, y esas relaciones son LAZY
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    // Historial de una mascota, del más reciente al más antiguo
    @EntityGraph(attributePaths = { "medicamento", "veterinario" })
    List<Tratamiento> findByMascota_IdOrderByFechaDescIdDesc(Long mascotaId);

    // Para desligar los tratamientos al eliminar un dueño
    List<Tratamiento> findByMascota_Dueno_Id(Long duenoId);

    // "Mis pacientes": mascotas que trató el veterinario, con cuántos tratamientos y la fecha del último.
    // El join deja fuera los tratamientos cuya mascota se eliminó (mascota = null)
    @Query("""
            select new com.veterinaria.dogtor.dto.PacienteResponse(m.id, m.nombre, m.raza, m.foto, m.activa,
                d.nombre, d.cedula, count(t), max(t.fecha))
            from Tratamiento t join t.mascota m join m.dueno d
            where t.veterinario.id = :veterinarioId
            group by m.id, m.nombre, m.raza, m.foto, m.activa, d.nombre, d.cedula
            order by max(t.fecha) desc, m.nombre
            """)
    List<PacienteResponse> pacientesDe(@Param("veterinarioId") Long veterinarioId);

    // ===== Dashboard (incluye los tratamientos de mascotas eliminadas: el historial se conserva) =====

    // Tratamientos y unidades por medicamento entre dos fechas (ambas incluidas)
    @Query("""
            select new com.veterinaria.dogtor.dto.MedicamentoCantidad(m.nombre, count(t), sum(t.cantidad))
            from Tratamiento t join t.medicamento m
            where t.fecha between :desde and :hasta
            group by m.id, m.nombre
            order by count(t) desc, sum(t.cantidad) desc, m.nombre
            """)
    List<MedicamentoCantidad> porMedicamentoEntre(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // Medicamentos con más unidades vendidas en tratamientos (desempate: más ventas)
    @Query("""
            select new com.veterinaria.dogtor.dto.TopMedicamento(m.nombre, sum(t.cantidad), sum(t.precioVenta * t.cantidad))
            from Tratamiento t join t.medicamento m
            group by m.id, m.nombre
            order by sum(t.cantidad) desc, sum(t.precioVenta * t.cantidad) desc, m.nombre
            """)
    List<TopMedicamento> topMedicamentos(Pageable pagina);

    @Query("select coalesce(sum(t.precioVenta * t.cantidad), 0.0) from Tratamiento t")
    Double ventasTotales();

    @Query("select coalesce(sum((t.precioVenta - t.precioCompra) * t.cantidad), 0.0) from Tratamiento t")
    Double gananciasTotales();
}
