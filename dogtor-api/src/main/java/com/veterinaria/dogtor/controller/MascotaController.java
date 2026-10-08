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

import com.veterinaria.dogtor.dto.EstadoMascotaRequest;
import com.veterinaria.dogtor.dto.MascotaRequest;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.entities.Tratamiento;
import com.veterinaria.dogtor.service.MascotaService;
import com.veterinaria.dogtor.service.TratamientoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

// CRUD de mascotas del portal veterinario. No hay DELETE: las mascotas solo se activan o desactivan
@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "http://localhost:4200")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private TratamientoService tratamientoService;

    @Operation(summary = "Listar mascotas (filtro opcional por nombre, raza o dueño)")
    @GetMapping
    public List<Mascota> listar(@RequestParam(required = false) String buscar) {
        return mascotaService.listar(buscar);
    }

    @Operation(summary = "Buscar una mascota por su id")
    @GetMapping("/{id}")
    public Mascota buscarPorId(@PathVariable Long id) {
        return mascotaService.buscarPorId(id);
    }

    @Operation(summary = "Historial de tratamientos de una mascota")
    @GetMapping("/{id}/tratamientos")
    public List<Tratamiento> tratamientos(@PathVariable Long id) {
        return tratamientoService.historialDe(id);
    }

    @Operation(summary = "Registrar una mascota de un dueño")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mascota crear(@Valid @RequestBody MascotaRequest request) {
        return mascotaService.crear(request);
    }

    @Operation(summary = "Actualizar los datos de una mascota")
    @PutMapping("/{id}")
    public Mascota actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
        return mascotaService.actualizar(id, request);
    }

    @Operation(summary = "Activar (en la clínica) o desactivar (en casa) una mascota")
    @PutMapping("/{id}/estado")
    public Mascota cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoMascotaRequest request) {
        return mascotaService.cambiarEstado(id, request.activa());
    }
}
