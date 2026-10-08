package com.veterinaria.dogtor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Dueno;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    Optional<Dueno> findByCedula(String cedula);

    Optional<Dueno> findByUsuarioId(Long usuarioId);

    boolean existsByCedula(String cedula);

    // Busca el texto en nombre, cédula, celular o correo (sin distinguir mayúsculas)
    @Query("""
            select d from Dueno d join d.usuario u
            where lower(d.nombre) like lower(concat('%', :texto, '%'))
               or d.cedula like concat('%', :texto, '%')
               or d.celular like concat('%', :texto, '%')
               or lower(u.correo) like lower(concat('%', :texto, '%'))
            order by d.nombre
            """)
    List<Dueno> buscar(@Param("texto") String texto);
}
