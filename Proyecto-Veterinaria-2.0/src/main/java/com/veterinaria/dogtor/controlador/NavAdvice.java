package com.veterinaria.dogtor.controlador;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavAdvice {

    @ModelAttribute("rol")
    public String rol(HttpServletRequest request) {
        Object rol = request.getAttribute("rol");
        return rol != null ? rol.toString() : null;
    }
}
