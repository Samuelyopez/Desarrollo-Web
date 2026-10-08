package com.veterinaria.dogtor.service;

import com.veterinaria.dogtor.dto.DashboardResponse;

public interface DashboardService {

    // KPIs del negocio; "último mes" = los últimos 30 días incluyendo hoy
    DashboardResponse obtener();
}
