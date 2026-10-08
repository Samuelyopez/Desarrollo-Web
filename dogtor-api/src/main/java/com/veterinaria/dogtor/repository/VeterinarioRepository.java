package com.veterinaria.dogtor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByCedula(String cedula);

    Optional<Veterinario> findByUsuarioId(Long usuarioId);

    boolean existsByCedula(String cedula);

    // Texto vacío = todos. Busca en nombre, cédula, especialidad o correo; activo null = cualquier estado
    @Query("""
            select v from Veterinario v join v.usuario u
            where (:activo is null or u.activo = :activo)
              and (:texto = ''
                   or lower(v.nombre) like lower(concat('%', :texto, '%'))
                   or v.cedula like concat('%', :texto, '%')
                   or lower(coalesce(v.especialidad, '')) like lower(concat('%', :texto, '%'))
                   or lower(u.correo) like lower(concat('%', :texto, '%')))
            order by v.nombre
            """)
    List<Veterinario> buscar(@Param("texto") String texto, @Param("activo") Boolean activo);
}
