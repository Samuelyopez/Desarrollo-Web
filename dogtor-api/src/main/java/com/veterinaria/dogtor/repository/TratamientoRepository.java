package com.veterinaria.dogtor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.dto.PacienteResponse;
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
}
