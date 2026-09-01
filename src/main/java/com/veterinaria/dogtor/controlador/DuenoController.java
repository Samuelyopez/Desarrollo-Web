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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DuenoController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    private Dueno getDuenoLogueado(HttpSession session) {
        return (Dueno) session.getAttribute("usuarioLogueado");
    }

    private boolean isAdmin(HttpSession session) {
        Dueno dueno = getDuenoLogueado(session);
        return dueno != null && dueno.isAdmin();
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Dueno dueno) {
        dueno.setAdmin(false);
        duenoService.save(dueno);
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.findById(duenoSession.getId());
        List<Mascota> mascotas = mascotaService.findByDueno(dueno);
        model.addAttribute("dueno", dueno);
        model.addAttribute("mascotas", mascotas);
        return "perfil";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.findById(duenoSession.getId());
        model.addAttribute("dueno", dueno);
        return "editar-perfil";
    }

    @PostMapping("/perfil/editar")
    public String guardarPerfilEditar(@ModelAttribute Dueno formDueno, HttpSession session) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dbDueno = duenoService.findById(duenoSession.getId());
        dbDueno.setNombre(formDueno.getNombre());
        dbDueno.setCorreo(formDueno.getCorreo());
        dbDueno.setPassword(formDueno.getPassword());
        
        duenoService.save(dbDueno);
        session.setAttribute("usuarioLogueado", dbDueno);
        
        return "redirect:/perfil";
    }

    @GetMapping("/admin/duenos")
    public String adminDuenos(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("duenos", duenoService.findAll());
        return "admin-duenos";
    }

    @GetMapping("/admin/duenos/editar/{id}")
    public String adminEditarDueno(@PathVariable("id") Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.findById(id);
        model.addAttribute("dueno", dueno);
        return "admin-editar-dueno";
    }

    @PostMapping("/admin/duenos/editar")
    public String adminGuardarDueno(@ModelAttribute Dueno dueno, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dbDueno = duenoService.findById(dueno.getId());
        if (dbDueno != null) {
            dbDueno.setNombre(dueno.getNombre());
            dbDueno.setCorreo(dueno.getCorreo());
            dbDueno.setPassword(dueno.getPassword());
            dbDueno.setAdmin(dueno.isAdmin());
            duenoService.save(dbDueno);
        }
        return "redirect:/admin/duenos";
    }

    @GetMapping("/admin/duenos/borrar/{id}")
    public String adminBorrarDueno(@PathVariable("id") Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.findById(id);
        if (dueno != null) {
            List<Mascota> mascotas = mascotaService.findByDueno(dueno);
            for (Mascota m : mascotas) {
                mascotaService.delete(m.getId());
            }
            duenoService.delete(id);
        }
        return "redirect:/admin/duenos";
    }
}
