package com.veterinaria.dogtor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.dogtor.dto.DashboardResponse;
import com.veterinaria.dogtor.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;

// Dashboard del portal administrador
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Indicadores del negocio para el administrador (AC29)")
    @GetMapping
    public DashboardResponse obtener() {
        return dashboardService.obtener();
    }
}
