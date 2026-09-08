package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "pacientes";
    }

    @GetMapping("/mis-mascotas")
    public String listarMisMascotas(HttpSession session, Model model) {
        Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
        if (dueno == null) {
            return "redirect:/login";
        }
        List<Mascota> mascotas = mascotaService.findByDueno(dueno);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("dueno", dueno);
        return "pacientes";
    }

    @GetMapping("/{id}")
    public String verMascota(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("mascota", mascotaService.findById(id));
        return "detalle-mascota";
    }
}
