package com.veterinaria.dogtor.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/equipo")
    public String equipo() {
        return "equipo";
    }

    @GetMapping("/farmacia")
    public String farmacia() {
        return "farmacia";
    }

    @GetMapping("/adopciones")
    public String adopciones() {
        return "adopciones";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/citas")
    public String citas() {
        return "citas";
    }

    @GetMapping("/diagrama")
    public String diagrama() {
        return "diagrama";
    }
}
