package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.RegistroMedicoService;
import jakarta.servlet.http.HttpServletRequest;
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
    public String listarMisMascotas(HttpServletRequest request, Model model) {
        if (!"DUENO".equals(request.getAttribute("rol"))) {
            return "auth-shell";
        }
        Dueno dueno = (Dueno) request.getAttribute("usuarioLogueado");
        List<Mascota> mascotas = mascotaService.findByDueno(dueno);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("dueno", dueno);
        return "pacientes";
    }

    @GetMapping("/{id}")
    public String verMascota(@PathVariable("id") Integer id, HttpServletRequest request, Model model) {
        Mascota mascota = mascotaService.findById(id);
        if (mascota == null) {
            return "redirect:/pacientes";
        }
        model.addAttribute("mascota", mascota);

        Object rol = request.getAttribute("rol");
        boolean autorizado = "ADMIN".equals(rol) || "VETERINARIO".equals(rol);
        if ("DUENO".equals(rol)) {
            Dueno dueno = (Dueno) request.getAttribute("usuarioLogueado");
            autorizado = mascota.getDueno() != null
                    && dueno != null && mascota.getDueno().getId().equals(dueno.getId());
        }
        if (autorizado) {
            model.addAttribute("registros", registroMedicoService.findByMascota(mascota));
        }
        return "detalle-mascota";
    }
}
