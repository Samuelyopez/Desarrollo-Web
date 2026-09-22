package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.RegistroMedicoService;
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
    private final RegistroMedicoService registroMedicoService;

    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "pacientes";
    }

    @GetMapping("/mis-mascotas")
    public String listarMisMascotas(HttpSession session, Model model) {
        if (!"DUENO".equals(session.getAttribute("rol"))) {
            return "redirect:/login";
        }
        Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
        List<Mascota> mascotas = mascotaService.findByDueno(dueno);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("dueno", dueno);
        return "pacientes";
    }

    @GetMapping("/{id}")
    public String verMascota(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Mascota mascota = mascotaService.findById(id);
        model.addAttribute("mascota", mascota);

        Object rol = session.getAttribute("rol");
        boolean autorizado = "ADMIN".equals(rol) || "VETERINARIO".equals(rol);
        if ("DUENO".equals(rol)) {
            Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
            autorizado = mascota != null && mascota.getDueno() != null
                    && dueno != null && mascota.getDueno().getId().equals(dueno.getId());
        }
        if (autorizado) {
            model.addAttribute("registros", registroMedicoService.findByMascota(mascota));
        }
        return "detalle-mascota";
    }
}
