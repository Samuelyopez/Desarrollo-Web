package com.veterinaria.dogtor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.dto.DuenoRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;
import com.veterinaria.dogtor.service.DuenoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

// CRUD de dueños del portal veterinario. Los dueños se identifican por su cédula
@RestController
@RequestMapping("/api/duenos")
@CrossOrigin(origins = "http://localhost:4200")
public class DuenoController {

    @Autowired
    private DuenoService duenoService;

    @Operation(summary = "Listar dueños (filtro opcional por nombre, cédula, celular o correo)")
    @GetMapping
    public List<Dueno> listar(@RequestParam(required = false) String buscar) {
        return duenoService.listar(buscar);
    }

    @Operation(summary = "Buscar un dueño por su cédula")
    @GetMapping("/{cedula}")
    public Dueno buscarPorCedula(@PathVariable String cedula) {
        return duenoService.buscarPorCedula(cedula);
    }

    @Operation(summary = "Mascotas de un dueño")
    @GetMapping("/{cedula}/mascotas")
    public List<Mascota> mascotas(@PathVariable String cedula) {
        return duenoService.mascotasDe(cedula);
    }

    // Portal cliente: evita mostrarle a un dueño una mascota ajena. Ojo: sin token la API
    // no sabe quién llama, así que esto no reemplaza una seguridad real (fuera de alcance)
    @Operation(summary = "Buscar una mascota de un dueño (404 si no es suya)")
    @GetMapping("/{cedula}/mascotas/{id}")
    public Mascota mascota(@PathVariable String cedula, @PathVariable Long id) {
        return duenoService.mascotaDe(cedula, id);
    }

    @Operation(summary = "Registrar un dueño y su usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dueno crear(@Valid @RequestBody DuenoRequest request) {
        return duenoService.crear(request);
    }

    @Operation(summary = "Actualizar los datos de un dueño")
    @PutMapping("/{cedula}")
    public Dueno actualizar(@PathVariable String cedula, @Valid @RequestBody DuenoRequest request) {
        return duenoService.actualizar(cedula, request);
    }

    @Operation(summary = "Eliminar un dueño, sus mascotas y su usuario (conserva los tratamientos)")
    @DeleteMapping("/{cedula}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String cedula) {
        duenoService.eliminar(cedula);
    }
}
