package com.veterinaria.dogtor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.dto.EstadoVeterinarioRequest;
import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.dto.VeterinarioRequest;
import com.veterinaria.dogtor.entities.Veterinario;
import com.veterinaria.dogtor.service.VeterinarioService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

// CRUD de veterinarios del portal administrador, por cédula. No hay DELETE: se activan o desactivan.
// Ojo: /{id}/pacientes usa el id interno (lo pide el portal veterinario con su perfilId)
@RestController
@RequestMapping("/api/veterinarios")
@CrossOrigin(origins = "http://localhost:4200")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @Operation(summary = "Listar veterinarios (filtro opcional por texto y por estado)")
    @GetMapping
    public List<Veterinario> listar(@RequestParam(required = false) String buscar,
            @RequestParam(required = false) Boolean activo) {
        return veterinarioService.listar(buscar, activo);
    }

    @Operation(summary = "Buscar un veterinario por su cédula")
    @GetMapping("/{cedula}")
    public Veterinario buscarPorCedula(@PathVariable String cedula) {
        return veterinarioService.buscarPorCedula(cedula);
    }

    @Operation(summary = "Registrar un veterinario y su usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Veterinario crear(@Valid @RequestBody VeterinarioRequest request) {
        return veterinarioService.crear(request);
    }

    @Operation(summary = "Actualizar los datos de un veterinario")
    @PutMapping("/{cedula}")
    public Veterinario actualizar(@PathVariable String cedula, @Valid @RequestBody VeterinarioRequest request) {
        return veterinarioService.actualizar(cedula, request);
    }

    @Operation(summary = "Activar o desactivar un veterinario (vacaciones, incapacidad)")
    @PutMapping("/{cedula}/estado")
    public Veterinario cambiarEstado(@PathVariable String cedula, @Valid @RequestBody EstadoVeterinarioRequest request) {
        return veterinarioService.cambiarEstado(cedula, request.activo());
    }

    @Operation(summary = "Mascotas que ha tratado un veterinario (Mis pacientes)")
    @GetMapping("/{id}/pacientes")
    public List<PacienteResponse> pacientes(@PathVariable Long id) {
        return veterinarioService.pacientesDe(id);
    }
}
