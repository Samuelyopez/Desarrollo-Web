package com.veterinaria.dogtor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.service.MedicamentoService;

import io.swagger.v3.oas.annotations.Operation;

// Medicamentos cargados desde el Excel al iniciar la API
@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Operation(summary = "Listar medicamentos (opcional: solo con unidades disponibles)")
    @GetMapping
    public List<Medicamento> listar(@RequestParam(defaultValue = "false") boolean disponibles) {
        return medicamentoService.listar(disponibles);
    }
}
