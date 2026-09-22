package com.veterinaria.dogtor.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavAdvice {

    @ModelAttribute("rol")
    public String rol(HttpSession session) {
        Object rol = session.getAttribute("rol");
        return rol != null ? rol.toString() : null;
    }
}
