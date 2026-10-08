package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.entities.Medicamento;

public interface MedicamentoService {

    // Todos los medicamentos, o solo los que tienen unidades (desplegable de tratamientos)
    List<Medicamento> listar(boolean soloDisponibles);
}
