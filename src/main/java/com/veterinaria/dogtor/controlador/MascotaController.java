package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;
    private final DuenoService duenoService;

    private Dueno getDuenoLogueado(HttpSession session) {
        return (Dueno) session.getAttribute("usuarioLogueado");
    }

    private boolean isAdmin(HttpSession session) {
        Dueno dueno = getDuenoLogueado(session);
        return dueno != null && dueno.isAdmin();
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "pacientes";
    }

    @GetMapping("/pacientes/mis-mascotas")
    public String misMascotas() {
        return "redirect:/perfil";
    }

    @GetMapping("/pacientes/{id}")
    public String detalleMascota(@PathVariable("id") Integer id, Model model) {
        Mascota mascota = mascotaService.findById(id);
        model.addAttribute("mascota", mascota);
        return "detalle-mascota";
    }

    @GetMapping("/admin/mascotas")
    public String adminMascotas(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("mascotas", mascotaService.findAll());
        return "admin-mascotas";
    }

    @GetMapping("/admin/mascotas/nueva")
    public String adminNuevaMascota(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("duenos", duenoService.findAll());
        return "admin-formulario-mascota";
    }

    @PostMapping("/admin/mascotas/guardar")
    public String adminGuardarMascota(@ModelAttribute Mascota mascota, 
                                      @RequestParam("duenoId") Integer duenoId, 
                                      HttpSession session, 
                                      Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        Dueno dueno = duenoService.findById(duenoId);
        if (dueno == null) {
            model.addAttribute("error", "Dueño no encontrado");
            model.addAttribute("mascota", mascota);
            model.addAttribute("duenos", duenoService.findAll());
            return "admin-formulario-mascota";
        }
        
        mascota.setDueno(dueno);
        mascotaService.save(mascota);
        return "redirect:/admin/mascotas";
    }

    @GetMapping("/admin/mascotas/editar/{id}")
    public String adminEditarMascota(@PathVariable("id") Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Mascota mascota = mascotaService.findById(id);
        model.addAttribute("mascota", mascota);
        model.addAttribute("duenos", duenoService.findAll());
        return "admin-formulario-mascota";
    }

    @GetMapping("/admin/mascotas/borrar/{id}")
    public String adminBorrarMascota(@PathVariable("id") Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        mascotaService.delete(id);
        return "redirect:/admin/mascotas";
    }
}
