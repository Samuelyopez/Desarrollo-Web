package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MascotaController {

    Logger log = LoggerFactory.getLogger(MascotaController.class);

    @Autowired
    private MascotaService mascotaService;
    @Autowired
    private DuenoService duenoService;

    private Dueno getDuenoLogueado(HttpSession session) {
        return (Dueno) session.getAttribute("usuarioLogueado");
    }

    private boolean isAdmin(HttpSession session) {
        Dueno dueno = getDuenoLogueado(session);
        return dueno != null && dueno.isAdmin();
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("mascotas", mascotaService.searchAll());
        return "pacientes";
    }

    @GetMapping("/pacientes/mis-mascotas")
    public String misMascotas() {
        return "redirect:/perfil";
    }

    @GetMapping("/pacientes/{id}")
    public String detalleMascota(@PathVariable("id") Long id, Model model) {
        Mascota mascota = mascotaService.searchById(id);
        model.addAttribute("mascota", mascota);
        return "detalle-mascota";
    }

    @GetMapping("/admin/mascotas")
    public String adminMascotas(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("mascotas", mascotaService.searchAllIncluyendoInactivas());
        return "admin-mascotas";
    }

    @GetMapping("/admin/mascotas/nueva")
    public String adminNuevaMascota(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("duenos", duenoService.searchAll());
        return "admin-formulario-mascota";
    }

    @PostMapping("/admin/mascotas/guardar")
    public String adminGuardarMascota(@jakarta.validation.Valid @ModelAttribute("mascota") Mascota mascota, 
                                      org.springframework.validation.BindingResult bindingResult,
                                      @RequestParam(value = "duenoId", required = false) Long duenoId, 
                                      HttpSession session, 
                                      Model model,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("duenos", duenoService.searchAll());
            return "admin-formulario-mascota";
        }
        
        if (duenoId != null) {
            Dueno dueno = duenoService.searchById(duenoId);
            mascota.setDueno(dueno);
        } else {
            mascota.setDueno(null);
        }
        
        log.info("Guardando mascota");
        mascotaService.save(mascota);
        redirectAttributes.addFlashAttribute("mensaje", "Mascota guardada correctamente.");
        return "redirect:/admin/mascotas";
    }

    @GetMapping("/admin/mascotas/editar/{id}")
    public String adminEditarMascota(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Mascota mascota = mascotaService.searchById(id);
        model.addAttribute("mascota", mascota);
        model.addAttribute("duenos", duenoService.searchAll());
        return "admin-formulario-mascota";
    }

    @GetMapping("/admin/mascotas/toggle/{id}")
    public String adminToggleMascota(@PathVariable("id") Long id, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        mascotaService.toggleActivo(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de mascota actualizado.");
        return "redirect:/admin/mascotas";
    }
}
