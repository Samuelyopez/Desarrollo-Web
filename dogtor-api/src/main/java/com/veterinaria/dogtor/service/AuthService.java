package com.veterinaria.dogtor.service;

import com.veterinaria.dogtor.dto.LoginRequest;
import com.veterinaria.dogtor.dto.LoginResponse;

public interface AuthService {

    // Valida correo y contraseña. No crea sesión ni token: el front guarda el usuario en memoria
    LoginResponse login(LoginRequest request);
}
