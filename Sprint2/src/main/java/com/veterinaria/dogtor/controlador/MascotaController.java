package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.servicio.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pacientes")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "pacientes";
    }

    @GetMapping("/{id}")
    public String verMascota(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("mascota", mascotaService.findById(id));
        return "detalle-mascota";
    }
}
