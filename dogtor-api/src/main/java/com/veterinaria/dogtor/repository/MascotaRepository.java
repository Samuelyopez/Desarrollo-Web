package com.veterinaria.dogtor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Mascota;

// Todas las consultas traen el dueño en el mismo SELECT (@EntityGraph / join fetch):
// la mascota se serializa con duenoNombre y duenoCedula, y el dueño es LAZY
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @Override
    @EntityGraph(attributePaths = "dueno")
    Optional<Mascota> findById(Long id);

    @EntityGraph(attributePaths = "dueno")
    List<Mascota> findAllByOrderByNombreAsc();

    // Dueno_Id (con guion bajo) navega la relación; sin él choca con el getter Mascota.getDuenoId()
    @EntityGraph(attributePaths = "dueno")
    List<Mascota> findByDueno_Id(Long duenoId);

    // Una mascota solo si es de ese dueño (portal cliente)
    @EntityGraph(attributePaths = "dueno")
    Optional<Mascota> findByIdAndDueno_Id(Long id, Long duenoId);

    // Dashboard: mascotas que están en la veterinaria
    long countByActivaTrue();

    // Para saber si el dueño ya tiene una mascota con ese nombre
    Optional<Mascota> findByDueno_IdAndNombreIgnoreCase(Long duenoId, String nombre);

    // Busca el texto en el nombre o la raza de la mascota, o en el nombre o la cédula del dueño
    @Query("""
            select m from Mascota m join fetch m.dueno d
            where lower(m.nombre) like lower(concat('%', :texto, '%'))
               or lower(m.raza) like lower(concat('%', :texto, '%'))
               or lower(d.nombre) like lower(concat('%', :texto, '%'))
               or d.cedula like concat('%', :texto, '%')
            order by m.nombre
            """)
    List<Mascota> buscar(@Param("texto") String texto);
}
