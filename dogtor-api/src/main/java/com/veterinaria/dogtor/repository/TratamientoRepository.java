package com.veterinaria.dogtor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    // Mascotas tratadas por un veterinario ("Mis pacientes")
    @EntityGraph(attributePaths = { "medicamento", "veterinario", "mascota" })
    List<Tratamiento> findByVeterinario_Id(Long veterinarioId);
}
