package com.veterinaria.dogtor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.dogtor.entities.Medicamento;

import jakarta.persistence.LockModeType;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Medicamento> findAllByOrderByNombreAsc();

    // Para el desplegable de tratamientos: se llama con 0 (solo los que tienen unidades)
    List<Medicamento> findByUnidadesDisponiblesGreaterThanOrderByNombreAsc(Integer minimo);

    // Bloquea la fila hasta que termine la transacción: dos veterinarios no venden la misma unidad
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medicamento m where m.id = :id")
    Optional<Medicamento> findByIdParaActualizar(@Param("id") Long id);
}
