package com.veterinaria.dogtor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.service.VeterinarioService;

import io.swagger.v3.oas.annotations.Operation;

// El CRUD de veterinarios del portal administrador llega en el Sprint 6
@RestController
@RequestMapping("/api/veterinarios")
@CrossOrigin(origins = "http://localhost:4200")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @Operation(summary = "Mascotas que ha tratado un veterinario (Mis pacientes)")
    @GetMapping("/{id}/pacientes")
    public List<PacienteResponse> pacientes(@PathVariable Long id) {
        return veterinarioService.pacientesDe(id);
    }
}
