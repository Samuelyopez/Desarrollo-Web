package com.veterinaria.dogtor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.service.TratamientoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

// El historial de una mascota está en GET /api/mascotas/{id}/tratamientos
@RestController
@RequestMapping("/api/tratamientos")
@CrossOrigin(origins = "http://localhost:4200")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Operation(summary = "Dar un tratamiento a una mascota activa (descuenta unidades del medicamento)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tratamiento crear(@Valid @RequestBody TratamientoRequest request) {
        return tratamientoService.crear(request);
    }
}
